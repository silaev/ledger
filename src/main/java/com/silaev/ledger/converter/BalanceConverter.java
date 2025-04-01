package com.silaev.ledger.converter;

import com.silaev.ledger.model.Account;
import com.silaev.ledger.response.AccountResponse;
import com.silaev.ledger.response.BalanceResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

// TODO: Add unit tests
@Component
@AllArgsConstructor
public class BalanceConverter {
    public BalanceResponse convert(Account account, BigDecimal amount) {
        return BalanceResponse.builder()
            .account(new AccountResponse(Objects.requireNonNull(account, "account cannot be null").id()))
            .amount(Objects.requireNonNull(amount, "amount cannot be null"))
            .build();
    }
}
