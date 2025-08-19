package com.febrie.rpg.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import net.kyori.adventure.text.Component;
/**
 * 재시도 로직을 위한 유틸리티 클래스
 * 네트워크 오류나 일시적인 실패에 대해 자동 재시도 기능을 제공
 * 
 * @author CoffeeTory
 */
public class RetryUtil {
    
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_RETRY_DELAY = 1000; // 1초
    private static final double BACKOFF_MULTIPLIER = 2.0; // 지수 백오프
    
    /**
     * 기본 설정으로 재시도 실행
     */
    public static <T> CompletableFuture<T> executeWithRetry(
            @NotNull Supplier<CompletableFuture<T>> operation) {
        return executeWithRetry(operation, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY, null);
    }
    
    /**
     * 기본값과 함께 재시도 실행
     */
    public static <T> CompletableFuture<T> executeWithRetry(
            @NotNull Supplier<CompletableFuture<T>> operation,
            @Nullable T defaultValue) {
        return executeWithRetry(operation, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY, defaultValue);
    }
    
    /**
     * 커스텀 설정으로 재시도 실행
     */
    public static <T> CompletableFuture<T> executeWithRetry(
            @NotNull Supplier<CompletableFuture<T>> operation,
            int maxRetries,
            long retryDelay,
            @Nullable T defaultValue) {
        return executeWithRetryInternal(operation, 0, maxRetries, retryDelay, defaultValue);
    }
    
    /**
     * 재시도 로직 내부 구현
     */
    private static <T> CompletableFuture<T> executeWithRetryInternal(
            @NotNull Supplier<CompletableFuture<T>> operation,
            int currentAttempt,
            int maxRetries,
            long retryDelay,
            @Nullable T defaultValue) {
        
        return operation.get()
            .exceptionally(ex -> {
                if (currentAttempt < maxRetries) {
                    // 지수 백오프로 지연 시간 계산
                    long delay = (long) (retryDelay * Math.pow(BACKOFF_MULTIPLIER, currentAttempt));
                    
                    LogUtil.warning(String.format(
                        "작업 실패, 재시도 중... (시도 %d/%d, %dms 후)",
                        currentAttempt + 1, maxRetries, delay
                    ));
                    
                    // 지연 후 재시도
                    CompletableFuture<T> future = new CompletableFuture<>();
                    CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS)
                        .execute(() -> {
                            executeWithRetryInternal(
                                operation, 
                                currentAttempt + 1, 
                                maxRetries, 
                                retryDelay, 
                                defaultValue
                            ).whenComplete((result, error) -> {
                                if (error != null) {
                                    future.completeExceptionally(error);
                                } else {
                                    future.complete(result);
                                }
                            });
                        });
                    return future.join();
                }
                
                LogUtil.error(String.format(
                    "작업이 %d번의 시도 후에도 실패했습니다", 
                    maxRetries
                ), ex);
                
                if (defaultValue != null) {
                    return defaultValue;
                }
                throw new RuntimeException("모든 재시도가 실패했습니다", ex);
            });
    }
    
    /**
     * 특정 예외에 대해서만 재시도하는 버전
     */
    public static <T> CompletableFuture<T> executeWithRetryOn(
            @NotNull Supplier<CompletableFuture<T>> operation,
            @NotNull Class<? extends Throwable> retryableException,
            int maxRetries,
            long retryDelay,
            @Nullable T defaultValue) {
        
        return operation.get()
            .exceptionally(ex -> {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                
                // 재시도 가능한 예외인지 확인
                if (retryableException.isInstance(cause)) {
                    return executeWithRetry(operation, maxRetries, retryDelay, defaultValue).join();
                }
                
                // 재시도 불가능한 예외는 그대로 전파
                if (defaultValue != null) {
                    return defaultValue;
                }
                throw new RuntimeException("재시도 불가능한 예외", ex);
            });
    }
}