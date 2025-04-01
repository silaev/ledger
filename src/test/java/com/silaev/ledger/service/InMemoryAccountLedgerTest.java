package com.silaev.ledger.service;

import com.silaev.ledger.exception.IncorrectTransactionTypeException;
import com.silaev.ledger.exception.InsufficientFundsException;
import com.silaev.ledger.model.Account;
import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.model.TransactionType;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class InMemoryAccountLedgerTest {
    private final AccountLedger accountLedger = new InMemoryAccountLedger();

    @Test
    void shouldDebitTransaction() {
        // GIVEN
        var tx = Transaction.builder()
            .account(new Account(UUID.randomUUID()))
            .type(TransactionType.DEBIT)
            .timestamp(Instant.now())
            .amount(new BigDecimal("10.20"))
            .build();

        // WHEN
        var debited = accountLedger.debit(tx);

        // THEN
        assertThat(debited).isEqualTo(tx.amount());
    }

    @Test
    void shouldNotDebitTransactionWhenIncorrectTransactionType() {
        // GIVEN
        var tx = Transaction.builder()
            .account(new Account(UUID.randomUUID()))
            .type(TransactionType.CREDIT)
            .timestamp(Instant.now())
            .amount(new BigDecimal("10.20"))
            .build();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> accountLedger.debit(tx);

        // THEN
        assertThatThrownBy(throwingCallable).isInstanceOf(IncorrectTransactionTypeException.class);
    }

    @Test
    void shouldNotCreditTransactionWhenAccountIsNotFound() {
        // GIVEN
        var tx = Transaction.builder()
            .account(new Account(UUID.randomUUID()))
            .type(TransactionType.DEBIT)
            .timestamp(Instant.now())
            .amount(new BigDecimal("10.20"))
            .build();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> accountLedger.credit(tx);

        // THEN
        assertThatThrownBy(throwingCallable).isInstanceOf(IncorrectTransactionTypeException.class);
    }

    @Test
    void shouldNotCreditTransactionWhenIncorrectTransactionType() {
        // GIVEN
        var tx = Transaction.builder()
            .account(new Account(UUID.randomUUID()))
            .type(TransactionType.DEBIT)
            .timestamp(Instant.now())
            .amount(new BigDecimal("10.20"))
            .build();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> accountLedger.credit(tx);

        // THEN
        assertThatThrownBy(throwingCallable).isInstanceOf(IncorrectTransactionTypeException.class);
    }

    @Test
    void shouldCreditTransaction() {
        // GIVEN
        var account = new Account(UUID.randomUUID());
        var txDebit = Transaction.builder()
            .account(account)
            .type(TransactionType.DEBIT)
            .timestamp(Instant.now())
            .amount(new BigDecimal("10.20"))
            .build();
        accountLedger.debit(txDebit);
        var txCredit = Transaction.builder()
            .account(account)
            .type(TransactionType.CREDIT)
            .timestamp(Instant.now())
            .amount(new BigDecimal("9.33"))
            .build();
        var expectedBalance = new BigDecimal("0.87");

        // WHEN
        var credited = accountLedger.credit(txCredit);

        // THEN
        assertThat(credited).isEqualTo(expectedBalance);
    }

    @Test
    void shouldNotCreditWhenInsufficientFunds() {
        // GIVEN
        var account = new Account(UUID.randomUUID());
        var txDebit = Transaction.builder()
            .account(account)
            .type(TransactionType.DEBIT)
            .timestamp(Instant.now())
            .amount(new BigDecimal("10.20"))
            .build();
        accountLedger.debit(txDebit);
        var txCredit = Transaction.builder()
            .account(account)
            .type(TransactionType.CREDIT)
            .timestamp(Instant.now())
            .amount(new BigDecimal("10.33"))
            .build();

        // WHEN
        ThrowableAssert.ThrowingCallable throwingCallable = () -> accountLedger.credit(txCredit);

        // THEN
        assertThatThrownBy(throwingCallable).isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void shouldGetBalance() {
        // GIVEN
        var account = new Account(UUID.randomUUID());
        var txsDebit = IntStream.rangeClosed(1, 10).mapToObj(it ->
            Transaction.builder()
                .account(account)
                .type(TransactionType.DEBIT)
                .timestamp(Instant.now())
                .amount(new BigDecimal("15.32").multiply(new BigDecimal(it)))
                .build()
        ).toList();
        for (var tx : txsDebit) {
            accountLedger.debit(tx);
        }
        var txsCredit = IntStream.rangeClosed(1, 10).mapToObj(it ->
            Transaction.builder()
                .account(account)
                .type(TransactionType.CREDIT)
                .timestamp(Instant.now())
                .amount(new BigDecimal("3.46").multiply(new BigDecimal(it)))
                .build()
        ).toList();
        for (var tx : txsCredit) {
            accountLedger.credit(tx);
        }
        var expectedAmount = txsDebit.stream().map(Transaction::amount).reduce(BigDecimal.ZERO, BigDecimal::add)
            .subtract(txsCredit.stream().map(Transaction::amount).reduce(BigDecimal.ZERO, BigDecimal::add));

        // WHEN
        var balance = accountLedger.getBalance(account);

        // THEN
        assertThat(balance).isEqualTo(expectedAmount);
    }
}
