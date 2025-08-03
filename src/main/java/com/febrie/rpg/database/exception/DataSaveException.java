package com.febrie.rpg.database.exception;

/**
 * 데이터 저장 실패 시 발생하는 예외
 *
 * @author Febrie
 */
public class DataSaveException extends DatabaseException {
    
    private final String operation;
    private final String dataType;
    
    public DataSaveException(String operation, String dataType, String message) {
        super(String.format("Failed to %s %s: %s", operation, dataType, message));
        this.operation = operation;
        this.dataType = dataType;
    }
    
    public DataSaveException(String operation, String dataType, Throwable cause) {
        super(String.format("Failed to %s %s", operation, dataType), cause);
        this.operation = operation;
        this.dataType = dataType;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getDataType() {
        return dataType;
    }
}