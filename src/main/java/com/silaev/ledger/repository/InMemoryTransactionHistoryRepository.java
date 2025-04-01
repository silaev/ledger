package com.silaev.ledger.repository;

import com.silaev.ledger.exception.NotFoundException;
import com.silaev.ledger.model.Account;
import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.request.GetHistoryFilter;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTransactionHistoryRepository implements TransactionHistoryRepository {
    private final Map<Account, NavigableMap<Instant, List<Transaction>>> userTransactionHistory = new ConcurrentHashMap<>();

    @Override
    public int append(Transaction transaction) {
        Objects.requireNonNull(transaction, "transaction must not be null");
        userTransactionHistory.compute(transaction.account(), (existingKey, existingValue) ->
            {
                final var sortedIdx = Optional.ofNullable(existingValue).orElseGet(TreeMap::new);
                sortedIdx.computeIfAbsent(transaction.timestamp(), key -> new ArrayList<>()).add(transaction);
                return sortedIdx;
            }
        );
        return userTransactionHistory.get(transaction.account()).size();
    }

    @Override
    public NavigableMap<Instant, List<Transaction>> search(GetHistoryFilter historyFilter) {
        Objects.requireNonNull(historyFilter, "historyFilter must not be null");
        final var account = new Account(historyFilter.accountId());
        final var sortedIdx = Optional.ofNullable(userTransactionHistory.get(account)).orElseThrow(() -> new NotFoundException("Account: " + historyFilter.accountId() + " is not found"));
        return sortedIdx.subMap(historyFilter.fromInUTC(), true, historyFilter.toInUTC(), true);
    }
}
