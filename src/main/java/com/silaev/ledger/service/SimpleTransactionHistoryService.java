package com.silaev.ledger.service;

import com.silaev.ledger.converter.TransactionHistoryConverter;
import com.silaev.ledger.repository.TransactionHistoryRepository;
import com.silaev.ledger.request.GetHistoryFilter;
import com.silaev.ledger.response.TransactionHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimpleTransactionHistoryService implements TransactionHistoryService {
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionHistoryConverter transactionHistoryConverter;

    @Override
    public List<TransactionHistoryResponse> getHistory(GetHistoryFilter historyFilter) {
        return transactionHistoryConverter.convert(transactionHistoryRepository.search(historyFilter), historyFilter.zoneId());
    }
}
