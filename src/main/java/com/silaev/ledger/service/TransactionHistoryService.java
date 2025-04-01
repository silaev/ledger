package com.silaev.ledger.service;

import com.silaev.ledger.request.GetHistoryFilter;
import com.silaev.ledger.response.TransactionHistoryResponse;

import java.util.List;

public interface TransactionHistoryService {
    List<TransactionHistoryResponse> getHistory(GetHistoryFilter historyFilter);
}
