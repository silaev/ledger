package com.silaev.ledger.response;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class BalanceResponseWithStats extends BalanceResponse {
    private final int totalTransactionNumber;
}
