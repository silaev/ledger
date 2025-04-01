package com.silaev.ledger.service;

import com.silaev.ledger.converter.BalanceConverter;
import com.silaev.ledger.converter.BalanceWithStatsConverter;
import com.silaev.ledger.model.Account;
import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.repository.TransactionHistoryRepository;
import com.silaev.ledger.response.BalanceResponse;
import com.silaev.ledger.response.BalanceResponseWithStats;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class SimpleLedgerServiceTest {
    private final AccountLedger accountLedger = mock(AccountLedger.class);
    private final TransactionHistoryRepository transactionHistoryRepository = mock(TransactionHistoryRepository.class);
    private final BalanceWithStatsConverter balanceWithStatsConverter = mock(BalanceWithStatsConverter.class);
    private final BalanceConverter balanceConverter = mock(BalanceConverter.class);
    private final LedgerService ledgerService = new SimpleLedgerService(accountLedger, transactionHistoryRepository, balanceConverter, balanceWithStatsConverter);

    @Test
    void shouldDeposit() {
        // GIVEN
        var tx = mock(Transaction.class);
        var account = mock(Account.class);
        given(tx.account()).willReturn(account);
        var balance = mock(BalanceResponseWithStats.class);
        var amount = ONE;
        given(accountLedger.debit(tx)).willReturn(amount);
        given(balanceWithStatsConverter.convert(account, amount, 1)).willReturn(balance);
        given(transactionHistoryRepository.append(tx)).willReturn(1);

        // WHEN
        var balanceResponseActual = ledgerService.deposit(tx);

        // THEN
        assertThat(balanceResponseActual).isEqualTo(balance);
    }

    @Test
    void shouldWithdraw() {
        // GIVEN
        var tx = mock(Transaction.class);
        var account = mock(Account.class);
        given(tx.account()).willReturn(account);
        var balance = mock(BalanceResponseWithStats.class);
        var amount = ONE;
        given(accountLedger.credit(tx)).willReturn(amount);
        given(balanceWithStatsConverter.convert(account, amount, 1)).willReturn(balance);
        given(transactionHistoryRepository.append(tx)).willReturn(1);

        // WHEN
        var balanceResponseActual = ledgerService.withdraw(tx);

        // THEN
        assertThat(balanceResponseActual).isEqualTo(balance);
    }

    @Test
    void shouldGetBalance() {
        // GIVEN
        var accountId = UUID.randomUUID();
        var account = new Account(accountId);
        var balance = mock(BalanceResponse.class);
        var amount = ONE;
        given(accountLedger.getBalance(account)).willReturn(amount);
        given(balanceConverter.convert(account, accountLedger.getBalance(account))).willReturn(balance);

        // WHEN
        var balanceResponseActual = ledgerService.getBalance(accountId);

        // THEN
        assertThat(balanceResponseActual).isEqualTo(balance);
    }
}
