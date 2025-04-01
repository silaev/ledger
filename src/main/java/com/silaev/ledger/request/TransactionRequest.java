package com.silaev.ledger.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record TransactionRequest(
    @NotNull
    UUID accountId,

    @DecimalMin(value = "0.1")
    @NotNull
    BigDecimal amount
) {
}

