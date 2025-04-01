package com.silaev.ledger.exception;

public class IncorrectTransactionTypeException extends RuntimeException {
    public IncorrectTransactionTypeException(String message) {
        super(message);
    }
}