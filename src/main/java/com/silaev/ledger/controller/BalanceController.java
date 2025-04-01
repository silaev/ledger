package com.silaev.ledger.controller;

import com.silaev.ledger.response.BalanceResponse;
import com.silaev.ledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/balance")
@Validated
@RequiredArgsConstructor
public class BalanceController {
    private final LedgerService ledgerService;

    @GetMapping(value = "/{accountId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable UUID accountId) {
        return ResponseEntity.ok(ledgerService.getBalance(accountId));
    }
}
