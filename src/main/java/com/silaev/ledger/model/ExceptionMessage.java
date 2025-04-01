package com.silaev.ledger.model;


import java.util.Objects;

public record ExceptionMessage(
    String message
    // TODO: Some other fields like errorCode etc are omitted for the sake of simplicity
) {
    public ExceptionMessage {
        Objects.requireNonNull(message, "message must not be null");
    }
}
