package com.silaev.ledger.converter;

import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.response.AccountResponse;
import com.silaev.ledger.response.TransactionHistoryResponse;
import com.silaev.ledger.response.TransactionResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;

// TODO: Add unit tests

/**
 * Input should be validated against null values to avoid NPE
 */
@Component
@AllArgsConstructor
public class TransactionHistoryConverter {
    public List<TransactionHistoryResponse> convert(NavigableMap<Instant, List<Transaction>> filteredSortedIdx, ZoneId zoneId) {
        return Objects.requireNonNull(filteredSortedIdx, "filteredSortedIdx cannot be null").entrySet().stream().map(
            entry -> TransactionHistoryResponse.builder()
                .timestamp(entry.getKey().atZone(Objects.requireNonNull(zoneId, "zoneId cannot be null")))
                .transactions(
                    entry.getValue().stream().map(
                        transaction -> TransactionResponse.builder()
                            .account(new AccountResponse(transaction.account().id()))
                            .amount(transaction.amount())
                            .type(transaction.type())
                            .build()
                    ).toList()
                ).build()
        ).toList();
    }
}
