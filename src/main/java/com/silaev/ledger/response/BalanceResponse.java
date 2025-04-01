package com.silaev.ledger.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@SuperBuilder
public class BalanceResponse {
    private final AccountResponse account;
    private final BigDecimal amount;

    public BalanceResponse(AccountResponse account, BigDecimal amount) {
        this.account = Objects.requireNonNull(account, "account cannot be null");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
    }
}
