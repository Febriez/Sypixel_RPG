package com.febrie.rpg.database.exception;

/**
 * 데이터를 찾을 수 없을 때 발생하는 예외
 *
 * @author Febrie
 */
public class DataNotFoundException extends DatabaseException {
    
    private final String dataType;
    private final String identifier;
    
    public DataNotFoundException(String dataType, String identifier) {
        super(String.format("%s not found: %s", dataType, identifier));
        this.dataType = dataType;
        this.identifier = identifier;
    }
    
    public DataNotFoundException(String dataType, String identifier, Throwable cause) {
        super(String.format("%s not found: %s", dataType, identifier), cause);
        this.dataType = dataType;
        this.identifier = identifier;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public String getIdentifier() {
        return identifier;
    }
}