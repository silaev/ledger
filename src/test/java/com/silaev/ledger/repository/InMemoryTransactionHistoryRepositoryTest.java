package com.silaev.ledger.repository;

import com.silaev.ledger.model.Account;
import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.model.TransactionType;
import com.silaev.ledger.request.GetHistoryFilter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class InMemoryTransactionHistoryRepositoryTest {
    public static final ZoneId PT_CONTINENT_ZONE_ID = ZoneId.of("Europe/Lisbon");
    private final TransactionHistoryRepository repository = new InMemoryTransactionHistoryRepository();

    private static Stream<Arguments> searchTestCases() {
        var accountId = UUID.randomUUID();
        var txs = getTxs(accountId);

        return Stream.of(
            Arguments.of(
                new GetHistoryFilter(
                    accountId,
                    LocalDateTime.of(2025, 4, 2, 14, 0, 0),
                    LocalDateTime.of(2025, 5, 5, 18, 0, 0),
                    PT_CONTINENT_ZONE_ID
                ),
                txs,
                txs.subList(2, txs.size() - 1)
            ),
            Arguments.of(
                new GetHistoryFilter(
                    accountId,
                    LocalDateTime.of(2025, 3, 31, 13, 0, 0),
                    LocalDateTime.of(2025, 5, 5, 19, 0, 0),
                    PT_CONTINENT_ZONE_ID
                ),
                txs,
                txs
            )
        );
    }

    private static List<Transaction> getTxs(UUID accountId) {
        var account = new Account(accountId);
        var txDebit1 = Transaction.builder()
            .account(account)
            .type(TransactionType.DEBIT)
            .timestamp(LocalDateTime.of(2025, 3, 31, 12, 0, 0).toInstant(ZoneOffset.UTC))
            .amount(new BigDecimal("5.50"))
            .build();
        var txDebit2 = Transaction.builder()
            .account(account)
            .type(TransactionType.DEBIT)
            .timestamp(LocalDateTime.of(2025, 3, 31, 12, 0, 0).toInstant(ZoneOffset.UTC))
            .amount(new BigDecimal("10.20"))
            .build();
        var txCredit1 = Transaction.builder()
            .account(account)
            .type(TransactionType.CREDIT)
            .timestamp(LocalDateTime.of(2025, 4, 2, 13, 0, 0).toInstant(ZoneOffset.UTC))
            .amount(new BigDecimal("2.50"))
            .build();
        var txCredit2 = Transaction.builder()
            .account(account)
            .type(TransactionType.CREDIT)
            .timestamp(LocalDateTime.of(2025, 4, 3, 13, 0, 0).toInstant(ZoneOffset.UTC))
            .amount(new BigDecimal("5"))
            .build();
        var txDebit3 = Transaction.builder()
            .account(account)
            .type(TransactionType.DEBIT)
            .timestamp(LocalDateTime.of(2025, 5, 5, 18, 0, 0).toInstant(ZoneOffset.UTC))
            .amount(new BigDecimal("5.50"))
            .build();
        return List.of(txDebit1, txDebit2, txCredit1, txCredit2, txDebit3);
    }

    @ParameterizedTest
    @MethodSource("searchTestCases")
    void shouldTestSearch(GetHistoryFilter searchFilter, List<Transaction> initialTx, List<Transaction> expectedTxs) {
        // GIVEN
        var expectedTxsIdx = expectedTxs.stream().collect(Collectors.groupingBy(Transaction::timestamp));

        // WHEN
        initialTx.forEach(repository::append);
        var actualSearchResult = repository.search(searchFilter);

        // THEN
        assertThat(actualSearchResult).hasSize(expectedTxsIdx.size()).isEqualTo(expectedTxsIdx);
    }
}
