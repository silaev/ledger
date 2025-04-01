package com.silaev.ledger.response;

import com.silaev.ledger.model.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Objects;

@Builder
public record TransactionResponse(
    AccountResponse account,
    BigDecimal amount,
    TransactionType type
) {
    public TransactionResponse {
        Objects.requireNonNull(account, "account must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(type, "type must not be null");
    }
}
