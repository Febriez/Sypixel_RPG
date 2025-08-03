package com.febrie.rpg.database.exception;

/**
 * 트랜잭션 처리 중 발생하는 예외
 *
 * @author Febrie
 */
public class TransactionException extends DatabaseException {
    
    private final String transactionType;
    private final int attemptNumber;
    
    public TransactionException(String transactionType, String message) {
        super(String.format("Transaction failed [%s]: %s", transactionType, message));
        this.transactionType = transactionType;
        this.attemptNumber = 0;
    }
    
    public TransactionException(String transactionType, int attemptNumber, Throwable cause) {
        super(String.format("Transaction failed [%s] after %d attempts", transactionType, attemptNumber), cause);
        this.transactionType = transactionType;
        this.attemptNumber = attemptNumber;
    }
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public int getAttemptNumber() {
        return attemptNumber;
    }
}