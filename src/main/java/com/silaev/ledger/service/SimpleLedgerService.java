package com.silaev.ledger.service;

import com.silaev.ledger.converter.BalanceConverter;
import com.silaev.ledger.converter.BalanceWithStatsConverter;
import com.silaev.ledger.model.Account;
import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.repository.TransactionHistoryRepository;
import com.silaev.ledger.response.BalanceResponse;
import com.silaev.ledger.response.BalanceResponseWithStats;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class SimpleLedgerService implements LedgerService {
    private final AccountLedger accountLedger;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final BalanceConverter balanceConverter;
    private final BalanceWithStatsConverter balanceWithStatsConverter;


    @Override
    public BalanceResponseWithStats deposit(Transaction transaction) {
        final var debited = accountLedger.debit(transaction);
        final var totalTxNum = transactionHistoryRepository.append(transaction);
        return balanceWithStatsConverter.convert(transaction.account(), debited, totalTxNum);
    }

    @Override
    public BalanceResponseWithStats withdraw(Transaction transaction) {
        final var credited = accountLedger.credit(transaction);
        final var totalTxNum = transactionHistoryRepository.append(transaction);
        return balanceWithStatsConverter.convert(transaction.account(), credited, totalTxNum);
    }

    @Override
    public BalanceResponse getBalance(UUID accountId) {
        final var account = new Account(accountId);
        return balanceConverter.convert(account, accountLedger.getBalance(account));
    }
}
