package com.febrie.rpg.database.exception;

/**
 * 데이터 검증 실패 시 발생하는 예외
 *
 * @author Febrie
 */
public class DataValidationException extends DatabaseException {
    
    private final String fieldName;
    private final Object invalidValue;
    private final String validationRule;
    
    public DataValidationException(String fieldName, Object invalidValue, String validationRule) {
        super(String.format("Validation failed for field '%s': value '%s' violates rule '%s'", 
                fieldName, invalidValue, validationRule));
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.validationRule = validationRule;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getInvalidValue() {
        return invalidValue;
    }
    
    public String getValidationRule() {
        return validationRule;
    }
}