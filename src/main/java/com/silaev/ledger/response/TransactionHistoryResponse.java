package com.silaev.ledger.response;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Builder
public record TransactionHistoryResponse(
    ZonedDateTime timestamp,
    List<TransactionResponse> transactions
) {
    public TransactionHistoryResponse {
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        Objects.requireNonNull(transactions, "transactions must not be null");
    }
}
