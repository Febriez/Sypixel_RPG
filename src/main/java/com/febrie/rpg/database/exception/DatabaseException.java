package com.febrie.rpg.database.exception;

/**
 * 데이터베이스 관련 기본 예외 클래스
 *
 * @author Febrie
 */
public class DatabaseException extends Exception {
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}