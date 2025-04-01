package com.silaev.ledger.converter;

import com.silaev.ledger.model.Account;
import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.model.TransactionType;
import com.silaev.ledger.request.TransactionRequest;
import com.silaev.ledger.service.ApplicationClock;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

// TODO: Add unit tests
@Component
@AllArgsConstructor
public class TransactionRequestToTransactionConverter {
    private final ApplicationClock applicationClock;

    public Transaction convert(TransactionRequest source, TransactionType type) {
        Objects.requireNonNull(source, "TransactionRequest must not be null");
        return Transaction.builder()
            .account(new Account(source.accountId()))
            .type(Objects.requireNonNull(type, "TransactionType must not be null"))
            .timestamp(applicationClock.now())
            .amount(source.amount())
            .build();
    }
}
