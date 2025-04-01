package com.silaev.ledger.service;

import com.silaev.ledger.model.Account;
import com.silaev.ledger.model.Transaction;

import java.math.BigDecimal;

public interface AccountLedger {
    BigDecimal debit(Transaction transaction);

    BigDecimal credit(Transaction transaction);

    BigDecimal getBalance(Account account);
}
