package com.silaev.ledger.service;

import com.silaev.ledger.converter.TransactionHistoryConverter;
import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.repository.TransactionHistoryRepository;
import com.silaev.ledger.request.GetHistoryFilter;
import com.silaev.ledger.response.TransactionHistoryResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class SimpleTransactionHistoryServiceTest {

    TransactionHistoryRepository transactionHistoryRepository = mock(TransactionHistoryRepository.class);
    TransactionHistoryConverter transactionHistoryConverter = mock(TransactionHistoryConverter.class);
    private final TransactionHistoryService transactionHistoryService = new SimpleTransactionHistoryService(
        transactionHistoryRepository,
        transactionHistoryConverter
    );

    @Test
    void shouldGetHistory() {
        // GIVEN
        @SuppressWarnings("unchecked")
        NavigableMap<Instant, List<Transaction>> treeMap = mock(TreeMap.class);
        var filter = mock(GetHistoryFilter.class);
        var zoneId = ZoneId.of("Europe/Lisbon");
        given(filter.zoneId()).willReturn(zoneId);
        given(transactionHistoryRepository.search(filter)).willReturn(treeMap);
        var historyResponses = List.of(mock(TransactionHistoryResponse.class));
        given(transactionHistoryConverter.convert(treeMap, zoneId)).willReturn(historyResponses);

        // WHEN
        var history = transactionHistoryService.getHistory(filter);

        // THEN
        assertThat(history).isEqualTo(historyResponses);
    }
}
