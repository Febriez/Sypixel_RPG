package com.febrie.rpg.database;

import com.febrie.rpg.dto.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Firebase Firestore 데이터베이스 서비스
 * 모든 데이터베이스 작업을 담당 (순수 POJO DTO 지원)
 *
 * @author Febrie, CoffeeTory
 */
public class FirebaseService {

    private final Plugin plugin;
    private Firestore firestore;

    // 컬렉션 이름들
    private static final String PLAYERS_COLLECTION = "players";
    private static final String LEADERBOARDS_COLLECTION = "leaderboards";
    private static final String SERVER_STATS_COLLECTION = "server_stats";

    // 서브컬렉션 이름들
    private static final String STATS_SUBCOLLECTION = "stats";
    private static final String TALENTS_SUBCOLLECTION = "talents";
    private static final String PROGRESS_SUBCOLLECTION = "progress";

    public FirebaseService(@NotNull Plugin plugin) {
        this.plugin = plugin;
        initializeFirebase();
    }

    /**
     * Firebase 초기화
     */
    private void initializeFirebase() {
        try {
            // Firebase 설정 파일 경로
            FileInputStream serviceAccount = new FileInputStream(
                    plugin.getDataFolder().getPath() + "/firebase-credentials.json"
            );

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            firestore = FirestoreClient.getFirestore();
            plugin.getLogger().info("Firebase 초기화 완료!");

        } catch (IOException e) {
            logError("Firebase 초기화 실패", e);
        }
    }

    /**
     * 에러 로깅 헬퍼 메소드
     */
    private void logError(@NotNull String message, @NotNull Throwable throwable) {
        plugin.getLogger().severe(message + ": " + throwable.getMessage());
        if (plugin.getLogger().isLoggable(java.util.logging.Level.FINE)) {
            // 디버그 모드에서만 전체 스택 트레이스 출력
            for (StackTraceElement element : throwable.getStackTrace()) {
                plugin.getLogger().fine("  at " + element.toString());
            }
        }
        // 원인이 있는 경우
        if (throwable.getCause() != null) {
            plugin.getLogger().severe("원인: " + throwable.getCause().getMessage());
        }
    }

    // ========== 플레이어 데이터 작업 ==========

    /**
     * 플레이어 데이터 저장 (전체)
     */
    public CompletableFuture<Void> savePlayerData(@NotNull String uuid, @NotNull PlayerDTO playerDTO,
                                                  @NotNull StatsDTO statsDTO, @NotNull TalentDTO talentDTO,
                                                  @NotNull ProgressDTO progressDTO) {
        return CompletableFuture.runAsync(() -> {
            try {
                // 타임스탬프 업데이트
                playerDTO.updateLastSeen();
                statsDTO.markUpdated();
                talentDTO.markUpdated();
                progressDTO.markUpdated();

                WriteBatch batch = firestore.batch();

                // 플레이어 기본 정보
                DocumentReference playerRef = firestore.collection(PLAYERS_COLLECTION).document(uuid);
                batch.set(playerRef, playerDTO.toMap());

                // 스탯 정보
                DocumentReference statsRef = playerRef.collection(STATS_SUBCOLLECTION).document("current");
                batch.set(statsRef, statsDTO.toMap());

                // 특성 정보
                DocumentReference talentsRef = playerRef.collection(TALENTS_SUBCOLLECTION).document("current");
                batch.set(talentsRef, talentDTO.toMap());

                // 진행도 정보
                DocumentReference progressRef = playerRef.collection(PROGRESS_SUBCOLLECTION).document("current");
                batch.set(progressRef, progressDTO.toMap());

                // 배치 실행
                batch.commit().get();

                plugin.getLogger().info("플레이어 데이터 저장 완료: " + uuid);

            } catch (InterruptedException | ExecutionException e) {
                logError("플레이어 데이터 저장 실패", e);
            }
        });
    }

    /**
     * 플레이어 기본 정보 로드
     */
    @Nullable
    public CompletableFuture<PlayerDTO> loadPlayerData(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot document = firestore.collection(PLAYERS_COLLECTION)
                        .document(uuid)
                        .get()
                        .get();

                if (document.exists()) {
                    Map<String, Object> data = document.getData();
                    if (data != null) {
                        return PlayerDTO.fromMap(data);
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                logError("플레이어 데이터 로드 실패", e);
            }
            return null;
        });
    }

    /**
     * 플레이어 스탯 정보 로드
     */
    @Nullable
    public CompletableFuture<StatsDTO> loadPlayerStats(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot document = firestore.collection(PLAYERS_COLLECTION)
                        .document(uuid)
                        .collection(STATS_SUBCOLLECTION)
                        .document("current")
                        .get()
                        .get();

                if (document.exists()) {
                    Map<String, Object> data = document.getData();
                    if (data != null) {
                        return StatsDTO.fromMap(data);
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                logError("플레이어 스탯 로드 실패", e);
            }
            return null;
        });
    }

    /**
     * 플레이어 특성 정보 로드
     */
    @Nullable
    public CompletableFuture<TalentDTO> loadPlayerTalents(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot document = firestore.collection(PLAYERS_COLLECTION)
                        .document(uuid)
                        .collection(TALENTS_SUBCOLLECTION)
                        .document("current")
                        .get()
                        .get();

                if (document.exists()) {
                    Map<String, Object> data = document.getData();
                    if (data != null) {
                        return TalentDTO.fromMap(data);
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                logError("플레이어 특성 로드 실패", e);
            }
            return null;
        });
    }

    /**
     * 플레이어 진행도 정보 로드
     */
    @Nullable
    public CompletableFuture<ProgressDTO> loadPlayerProgress(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot document = firestore.collection(PLAYERS_COLLECTION)
                        .document(uuid)
                        .collection(PROGRESS_SUBCOLLECTION)
                        .document("current")
                        .get()
                        .get();

                if (document.exists()) {
                    Map<String, Object> data = document.getData();
                    if (data != null) {
                        return ProgressDTO.fromMap(data);
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                logError("플레이어 진행도 로드 실패", e);
            }
            return null;
        });
    }

    /**
     * 플레이어 전체 데이터 로드
     */
    public CompletableFuture<PlayerDataBundle> loadAllPlayerData(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 병렬로 모든 데이터 로드
                CompletableFuture<PlayerDTO> playerFuture = loadPlayerData(uuid);
                CompletableFuture<StatsDTO> statsFuture = loadPlayerStats(uuid);
                CompletableFuture<TalentDTO> talentsFuture = loadPlayerTalents(uuid);
                CompletableFuture<ProgressDTO> progressFuture = loadPlayerProgress(uuid);

                // 모든 Future 완료 대기
                CompletableFuture.allOf(playerFuture, statsFuture, talentsFuture, progressFuture).join();

                // null 체크와 함께 결과 가져오기
                PlayerDTO player = playerFuture.join();
                StatsDTO stats = statsFuture.join();
                TalentDTO talents = talentsFuture.join();
                ProgressDTO progress = progressFuture.join();

                return new PlayerDataBundle(player, stats, talents, progress);

            } catch (Exception e) {
                logError("플레이어 전체 데이터 로드 실패", e);
                return PlayerDataBundle.empty();
            }
        });
    }

    // ========== 순위표 작업 ==========

    /**
     * 순위표 업데이트
     */
    public CompletableFuture<Void> updateLeaderboard(@NotNull String type, @NotNull LeaderboardEntryDTO entry) {
        return CompletableFuture.runAsync(() -> {
            try {
                entry.markUpdated();

                firestore.collection(LEADERBOARDS_COLLECTION)
                        .document(type)
                        .collection("entries")
                        .document(entry.getUuid())
                        .set(entry.toMap())
                        .get();

            } catch (InterruptedException | ExecutionException e) {
                logError("순위표 업데이트 실패", e);
            }
        });
    }

    /**
     * 순위표 조회 (상위 N명)
     */
    public CompletableFuture<List<LeaderboardEntryDTO>> getTopPlayers(@NotNull String type, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<LeaderboardEntryDTO> entries = new ArrayList<>();

            try {
                QuerySnapshot querySnapshot = firestore.collection(LEADERBOARDS_COLLECTION)
                        .document(type)
                        .collection("entries")
                        .orderBy("score", Query.Direction.DESCENDING)
                        .limit(limit)
                        .get()
                        .get();

                int rank = 1;
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Map<String, Object> data = document.getData();
                    LeaderboardEntryDTO entry = LeaderboardEntryDTO.fromMap(data);
                    entry.setRank(rank++);
                    entries.add(entry);
                }

            } catch (InterruptedException | ExecutionException e) {
                logError("순위표 조회 실패", e);
            }

            return entries;
        });
    }

    // ========== 서버 통계 작업 ==========

    /**
     * 서버 통계 업데이트
     */
    public CompletableFuture<Void> updateServerStats(@NotNull Map<String, Object> stats) {
        return CompletableFuture.runAsync(() -> {
            try {
                // 타임스탬프 추가
                stats.put("lastUpdated", System.currentTimeMillis());

                firestore.collection(SERVER_STATS_COLLECTION)
                        .document("global")
                        .update(stats)
                        .get();

            } catch (InterruptedException | ExecutionException e) {
                logError("서버 통계 업데이트 실패", e);
            }
        });
    }

    /**
     * 정리 작업
     */
    public void shutdown() {
        if (firestore != null) {
            try {
                firestore.close();
                plugin.getLogger().info("Firebase 연결 종료");
            } catch (Exception e) {
                logError("Firebase 종료 실패", e);
            }
        }
    }

    /**
     * 플레이어 데이터 번들 (한번에 모든 데이터를 담는 클래스)
     */
    public static class PlayerDataBundle {
        public final PlayerDTO player;
        public final StatsDTO stats;
        public final TalentDTO talents;
        public final ProgressDTO progress;

        public PlayerDataBundle(@Nullable PlayerDTO player, @Nullable StatsDTO stats,
                                @Nullable TalentDTO talents, @Nullable ProgressDTO progress) {
            this.player = player;
            this.stats = stats;
            this.talents = talents;
            this.progress = progress;
        }

        /**
         * 빈 번들 생성 (모든 필드가 null)
         */
        public static PlayerDataBundle empty() {
            return new PlayerDataBundle(null, null, null, null);
        }

        /**
         * 모든 데이터가 로드되었는지 확인
         */
        public boolean isComplete() {
            return player != null && stats != null && talents != null && progress != null;
        }

        /**
         * 부분적으로라도 데이터가 있는지 확인
         */
        public boolean hasAnyData() {
            return player != null || stats != null || talents != null || progress != null;
        }

        /**
         * 누락된 데이터 타입 목록 반환
         */
        @NotNull
        public List<String> getMissingDataTypes() {
            List<String> missing = new ArrayList<>();
            if (player == null) missing.add("Player");
            if (stats == null) missing.add("Stats");
            if (talents == null) missing.add("Talents");
            if (progress == null) missing.add("Progress");
            return missing;
        }
    }
}