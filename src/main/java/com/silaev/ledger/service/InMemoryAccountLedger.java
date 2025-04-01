package com.silaev.ledger.service;

import com.silaev.ledger.exception.IncorrectTransactionTypeException;
import com.silaev.ledger.exception.InsufficientFundsException;
import com.silaev.ledger.exception.NotFoundException;
import com.silaev.ledger.model.Account;
import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.model.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.silaev.ledger.model.TransactionType.CREDIT;
import static com.silaev.ledger.model.TransactionType.DEBIT;

@Service
public class InMemoryAccountLedger implements AccountLedger {
    private final Map<Account, BigDecimal> idx = new ConcurrentHashMap<>();

    @Override
    public BigDecimal debit(Transaction transaction) {
        validateInput(transaction, DEBIT);
        final var account = transaction.account();
        final var amount = transaction.amount();
        return idx.compute(account, (existingKey, existingValue) -> Optional.ofNullable(existingValue).map(it -> it.add(amount)).orElse(amount));
    }

    @Override
    public BigDecimal credit(Transaction transaction) {
        validateInput(transaction, CREDIT);
        final var account = transaction.account();
        final var amount = transaction.amount();
        return idx.compute(
            account,
            (existingKey, existingValue) -> {
                final var balance = Optional.ofNullable(existingValue)
                    .orElseThrow(() -> new NotFoundException("Account: " + account + " is not found"));
                if (balance.compareTo(amount) < 0) {
                    throw new InsufficientFundsException("Insufficient funds for account: " + account);
                }
                return balance.subtract(amount);
            }
        );
    }

    @Override
    public BigDecimal getBalance(Account account) {
        return Optional.ofNullable(idx.get(account)).orElseThrow(() -> new NotFoundException("Account: " + account + " is not found"));
    }

    private void validateInput(Transaction transaction, TransactionType transactionType) {
        Objects.requireNonNull(transaction, "transaction must not be null");
        if (Objects.requireNonNull(transaction.type(), "transaction type must not be null") != transactionType) {
            throw new IncorrectTransactionTypeException("Incorrect transaction type");
        }
    }
}
