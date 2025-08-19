package com.febrie.rpg.database.helper;

import com.febrie.rpg.database.constants.DatabaseConstants;
import com.febrie.rpg.util.LogUtil;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
/**
 * Firestore 헬퍼 유틸리티
 * 배치 저장, 실시간 저장, 트랜잭션 등을 지원
 *
 * @author Febrie, CoffeeTory
 */
public class FirestoreHelper {
    private static final int BATCH_SIZE_LIMIT = DatabaseConstants.BATCH_SIZE_LIMIT;
    private static final int MAX_RETRY_ATTEMPTS = DatabaseConstants.MAX_RETRY_ATTEMPTS;
    private static final long INITIAL_RETRY_DELAY_MS = DatabaseConstants.INITIAL_RETRY_DELAY_MS;
    private final Firestore firestore;
    private final ScheduledExecutorService scheduler;
    private final Map<String, BatchWriteQueue> batchQueues;
    private final ConcurrentLinkedQueue<FailedOperation> failedOperations;
    public FirestoreHelper(@NotNull Firestore firestore) {
        this.firestore = firestore;
        this.scheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "FirestoreHelper-Scheduler");
            t.setDaemon(true);
            return t;
        });
        this.batchQueues = new ConcurrentHashMap<>();
        this.failedOperations = new ConcurrentLinkedQueue<>();
    }
    
    /**
     * FirestoreHelper 초기화 (생성 후 호출 필요)
     */
    public void initialize() {
        // 실패한 작업 재시도 스케줄러 (5분마다)
        scheduler.scheduleWithFixedDelay(this::retryFailedOperations, 5, 5, TimeUnit.MINUTES);
    }
    
    /**
     * 즉시 저장 (중요 데이터용)
     */
    @NotNull
    public CompletableFuture<Void> saveImmediate(@NotNull String collection, @NotNull String documentId, @NotNull Map<String, Object> data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ApiFuture<WriteResult> future = firestore.collection(collection).document(documentId).set(data, SetOptions.merge());
                future.get(DatabaseConstants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                return null;
            } catch (Exception e) {
                LogUtil.warning("즉시 저장 실패 [" + collection + "/" + documentId + "]: " + e.getMessage());
                // 실패 시 배치 큐로 폴백
                addToBatch(collection, documentId, data, DataPriority.HIGH);
                return null;
            }
        });
    }
    
    /**
     * 배치 큐에 추가
     */
    public void addToBatch(@NotNull String collection, @NotNull String documentId, @NotNull Map<String, Object> data, @NotNull DataPriority priority) {
        String queueKey = collection + ":" + priority.name();
        BatchWriteQueue queue = batchQueues.computeIfAbsent(queueKey, k -> new BatchWriteQueue(collection, priority, this::executeBatch));
        queue.add(documentId, data);
    }
    
    /**
     * 트랜잭션 실행
     */
    public <T> CompletableFuture<T> runTransaction(@NotNull Function<Transaction, T> transactionFunction) {
        return runTransactionWithRetry(transactionFunction, 0);
    }
    
    private <T> CompletableFuture<T> runTransactionWithRetry(@NotNull Function<Transaction, T> transactionFunction, int attemptNumber) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ApiFuture<T> future = firestore.runTransaction(transactionFunction::apply);
                return future.get(DatabaseConstants.TRANSACTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (Exception e) {
                if (attemptNumber < MAX_RETRY_ATTEMPTS - 1) {
                    long delayMs = INITIAL_RETRY_DELAY_MS * (long) Math.pow(DatabaseConstants.RETRY_BACKOFF_MULTIPLIER, attemptNumber);
                    LogUtil.debug("트랜잭션 재시도 중... (시도 " + (attemptNumber + 1) + "/" + MAX_RETRY_ATTEMPTS + ")");
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    return runTransactionWithRetry(transactionFunction, attemptNumber + 1).join();
                }
                LogUtil.severe("트랜잭션 실패 (최대 재시도 횟수 초과): " + e.getMessage());
                throw new CompletionException(e);
            }
        });
    }
    
    /**
     * 부분 업데이트 (특정 필드만)
     */
    public CompletableFuture<Void> updateFields(@NotNull String collection, @NotNull String documentId, @NotNull Map<String, Object> fields) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ApiFuture<WriteResult> future = firestore.collection(collection).document(documentId).update(fields);
                future.get(DatabaseConstants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                return null;
            } catch (Exception e) {
                LogUtil.warning("필드 업데이트 실패 [" + collection + "/" + documentId + "]: " + e.getMessage());
                // 실패 시 전체 데이터로 배치 큐에 추가
                addToBatch(collection, documentId, fields, DataPriority.HIGH);
                return null;
            }
        });
    }
    
    /**
     * 배치 실행
     */
    private CompletableFuture<Void> executeBatch(@NotNull String collection, @NotNull List<BatchWriteEntry> entries) {
        return CompletableFuture.runAsync(() -> {
            List<List<BatchWriteEntry>> chunks = partitionList(entries, BATCH_SIZE_LIMIT);
            for (List<BatchWriteEntry> chunk : chunks) {
                WriteBatch batch = firestore.batch();
                for (BatchWriteEntry entry : chunk) {
                    batch.set(firestore.collection(collection).document(entry.documentId), entry.data, SetOptions.merge());
                }
                try {
                    ApiFuture<List<WriteResult>> future = batch.commit();
                    future.get(30, TimeUnit.SECONDS);
                    LogUtil.debug("배치 저장 성공: " + collection + " (" + chunk.size() + "개 문서)");
                } catch (Exception e) {
                    LogUtil.severe("배치 저장 실패 [" + collection + "]: " + e.getMessage());
                    // 실패한 항목들을 재시도 큐에 추가
                    for (BatchWriteEntry entry : chunk) {
                        failedOperations.offer(new FailedOperation(collection, entry.documentId, entry.data, System.currentTimeMillis()));
                    }
                }
            }
        });
    }
    
    /**
     * 실패한 작업 재시도
     */
    private void retryFailedOperations() {
        if (failedOperations.isEmpty()) return;
        List<FailedOperation> toRetry = new ArrayList<>();
        FailedOperation op;
        long now = System.currentTimeMillis();
        // 5분 이상 지난 실패 작업만 재시도
        while ((op = failedOperations.poll()) != null) {
            if (now - op.timestamp > 300000) { // 5분
                toRetry.add(op);
            } else {
                failedOperations.offer(op); // 다시 큐에 넣기
                break;
            }
        }
        for (FailedOperation failedOp : toRetry) {
            addToBatch(failedOp.collection, failedOp.documentId, failedOp.data, DataPriority.LOW);
        }
        
        if (!toRetry.isEmpty()) {
            LogUtil.info("실패한 작업 " + toRetry.size() + "개 재시도 시작");
        }
    }
    
    /**
     * 배치 큐 플러시 (강제 실행)
     */
    public CompletableFuture<Void> flushAll() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (BatchWriteQueue queue : batchQueues.values()) {
            futures.add(queue.flush());
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0]));
    }
    
    /**
     * 리소스 정리
     */
    public void shutdown() {
        // 모든 배치 플러시
        flushAll().join();
        // 스케줄러 종료
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 리스트를 지정된 크기로 분할
     */
    private static <T> List<List<T>> partitionList(List<T> list, int partitionSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += partitionSize) {
            partitions.add(list.subList(i, Math.min(i + partitionSize, list.size())));
        }
        return partitions;
    }
    
    /**
     * 데이터 우선순위
     */
    public enum DataPriority {
        HIGH(DatabaseConstants.SAVE_INTERVAL_HIGH_PRIORITY),      // 1분
        MEDIUM(DatabaseConstants.SAVE_INTERVAL_MEDIUM_PRIORITY),   // 3분  
        LOW(DatabaseConstants.SAVE_INTERVAL_LOW_PRIORITY);      // 5분
        private final long saveIntervalMs;
        DataPriority(long saveIntervalMs) {
            this.saveIntervalMs = saveIntervalMs;
        }
        
        public long getSaveIntervalMs() {
            return saveIntervalMs;
        }
    }
    
    /**
     * 배치 쓰기 엔트리
     */
    private record BatchWriteEntry(String documentId, Map<String, Object> data) {
    }
    
    /**
     * 실패한 작업 정보
     */
    private record FailedOperation(String collection, String documentId, Map<String, Object> data, long timestamp) {
    }
    
    /**
     * 배치 쓰기 큐
     */
    private class BatchWriteQueue {
        private final String collection;
        private final DataPriority priority;
        private final Map<String, Map<String, Object>> pendingWrites;
        private final BiFunction<String, List<BatchWriteEntry>, CompletableFuture<Void>> batchExecutor;
        private volatile ScheduledFuture<?> scheduledFlush;
        BatchWriteQueue(String collection, DataPriority priority, BiFunction<String, List<BatchWriteEntry>, CompletableFuture<Void>> batchExecutor) {
            this.collection = collection;
            this.priority = priority;
            this.pendingWrites = new ConcurrentHashMap<>();
            this.batchExecutor = batchExecutor;
            scheduleNextFlush();
        }
        
        void add(String documentId, Map<String, Object> data) {
            pendingWrites.put(documentId, data);
            // 배치 크기 제한에 도달하면 즉시 플러시
            if (pendingWrites.size() >= BATCH_SIZE_LIMIT) {
                flush();
            }
        }
        
        CompletableFuture<Void> flush() {
            if (pendingWrites.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            
            // 현재 대기 중인 쓰기 작업들을 복사하고 클리어
            List<BatchWriteEntry> entries = new ArrayList<>();
            pendingWrites.forEach((id, data) -> entries.add(new BatchWriteEntry(id, new HashMap<>(data))));
            pendingWrites.clear();
            // 다음 플러시 재스케줄
            if (scheduledFlush != null) {
                scheduledFlush.cancel(false);
            }
            scheduleNextFlush();
            
            return batchExecutor.apply(collection, entries);
        }
        
        private void scheduleNextFlush() {
            // 3-5분 사이 랜덤 간격 (서버 부하 분산)
            long baseInterval = priority.getSaveIntervalMs();
            long randomOffset = priority == DataPriority.HIGH ? 0 : ThreadLocalRandom.current().nextLong(0, DatabaseConstants.SAVE_INTERVAL_RANDOM_OFFSET); // 0-2분 랜덤
            scheduledFlush = scheduler.schedule(this::flush, baseInterval + randomOffset, TimeUnit.MILLISECONDS);
        }
    }
}
