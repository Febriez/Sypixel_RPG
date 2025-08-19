package com.febrie.rpg.exception;

import com.febrie.rpg.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
/**
 * RPG 시스템 표준 예외
 * 모든 커스텀 예외의 기본 클래스
 *
 * @author Febrie, CoffeeTory
 */
public class RPGException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private final ErrorCode errorCode;
    private final String userMessage;
    
    public RPGException(@NotNull ErrorCode errorCode, @NotNull String message) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = errorCode.getUserMessage();
    }
    
    public RPGException(@NotNull ErrorCode errorCode, @NotNull String message, @Nullable Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = errorCode.getUserMessage();
    }
    
    public RPGException(@NotNull ErrorCode errorCode, @NotNull String message, @NotNull String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    /**
     * 로깅을 위한 정적 팩토리 메서드
     */
    public static RPGException createAndLog(@NotNull ErrorCode errorCode, @NotNull String message) {
        RPGException exception = new RPGException(errorCode, message);
        exception.logError();
        return exception;
    }
    
    public static RPGException createAndLog(@NotNull ErrorCode errorCode, @NotNull String message, @Nullable Throwable cause) {
        RPGException exception = new RPGException(errorCode, message, cause);
        exception.logError();
        return exception;
    }
    
    public static RPGException createAndLog(@NotNull ErrorCode errorCode, @NotNull String message, @NotNull String userMessage) {
        RPGException exception = new RPGException(errorCode, message, userMessage);
        exception.logError();
        return exception;
    }
    
    @NotNull
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    @NotNull
    public String getUserMessage() {
        return userMessage;
    }
    
    private void logError() {
        switch (errorCode.getSeverity()) {
            case LOW -> LogUtil.debug(String.format("[%s] %s", errorCode, getMessage()));
            case MEDIUM -> LogUtil.warning(String.format("[%s] %s", errorCode, getMessage()));
            case HIGH -> LogUtil.error(String.format("[%s] %s", errorCode, getMessage()));
            case CRITICAL -> LogUtil.severe(String.format("[%s] %s", errorCode, getMessage()));
        }
    }
    
    /**
     * 에러 코드 정의
     */
    public enum ErrorCode {
        // 데이터베이스 관련
        DB_CONNECTION_FAILED("DB001", "데이터베이스 연결 실패", Severity.CRITICAL),
        DB_QUERY_FAILED("DB002", "쿼리 실행 실패", Severity.HIGH),
        DB_DATA_NOT_FOUND("DB003", "데이터를 찾을 수 없음", Severity.MEDIUM),
        DB_SAVE_FAILED("DB004", "데이터 저장 실패", Severity.HIGH),
        
        // 플레이어 관련
        PLAYER_NOT_FOUND("PL001", "플레이어를 찾을 수 없음", Severity.MEDIUM),
        PLAYER_DATA_CORRUPT("PL002", "플레이어 데이터 손상", Severity.HIGH),
        PLAYER_PERMISSION_DENIED("PL003", "권한이 없습니다", Severity.LOW),
        
        // 아이템 관련
        ITEM_INVALID("IT001", "유효하지 않은 아이템", Severity.MEDIUM),
        ITEM_NOT_ENOUGH_SPACE("IT002", "인벤토리 공간 부족", Severity.LOW),
        
        // 퀘스트 관련
        QUEST_NOT_FOUND("QT001", "퀘스트를 찾을 수 없음", Severity.MEDIUM),
        QUEST_ALREADY_COMPLETED("QT002", "이미 완료한 퀘스트", Severity.LOW),
        QUEST_REQUIREMENTS_NOT_MET("QT003", "퀘스트 요구사항 미충족", Severity.LOW),
        
        // 섬 관련
        ISLAND_NOT_FOUND("IS001", "섬을 찾을 수 없음", Severity.MEDIUM),
        ISLAND_PERMISSION_DENIED("IS002", "섬 권한이 없습니다", Severity.LOW),
        ISLAND_MEMBER_LIMIT("IS003", "섬 멤버 제한 초과", Severity.LOW),
        
        // 시스템 관련
        SYSTEM_ERROR("SY001", "시스템 오류", Severity.HIGH),
        INVALID_CONFIGURATION("SY002", "잘못된 설정", Severity.HIGH),
        SERVICE_UNAVAILABLE("SY003", "서비스 일시적 이용 불가", Severity.MEDIUM);
        
        private final String code;
        private final String userMessage;
        private final Severity severity;
        
        ErrorCode(String code, String userMessage, Severity severity) {
            this.code = code;
            this.userMessage = userMessage;
            this.severity = severity;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getUserMessage() {
            return userMessage;
        }
        
        public Severity getSeverity() {
            return severity;
        }
        
        @Override
        public String toString() {
            return code;
        }
    }
    
    /**
     * 에러 심각도
     */
    public enum Severity {
        LOW,      // 정보성, 사용자 실수
        MEDIUM,   // 일반 오류
        HIGH,     // 중요 오류
        CRITICAL  // 치명적 오류
    }
}