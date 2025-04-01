package com.silaev.ledger.service;

import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.response.BalanceResponse;

import java.util.UUID;

public interface LedgerService {
    BalanceResponse deposit(Transaction transaction);

    BalanceResponse withdraw(Transaction transaction);

    BalanceResponse getBalance(UUID accountId);
}
