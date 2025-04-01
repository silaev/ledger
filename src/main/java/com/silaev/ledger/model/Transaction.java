package com.silaev.ledger.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Builder
public record Transaction(
    Account account,
    Instant timestamp,
    BigDecimal amount,
    TransactionType type
) {
    public Transaction {
        Objects.requireNonNull(account, "account must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(type, "type must not be null");
    }
}
