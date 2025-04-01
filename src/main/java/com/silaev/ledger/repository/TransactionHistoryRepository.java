package com.silaev.ledger.repository;

import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.request.GetHistoryFilter;

import java.time.Instant;
import java.util.List;
import java.util.NavigableMap;

public interface TransactionHistoryRepository {
    int append(Transaction transaction);

    NavigableMap<Instant, List<Transaction>> search(GetHistoryFilter historyFilter);
}
