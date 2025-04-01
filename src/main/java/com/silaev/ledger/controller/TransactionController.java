package com.silaev.ledger.controller;

import com.silaev.ledger.converter.TransactionRequestToTransactionConverter;
import com.silaev.ledger.request.GetHistoryFilter;
import com.silaev.ledger.request.TransactionRequest;
import com.silaev.ledger.response.BalanceResponse;
import com.silaev.ledger.response.TransactionHistoryResponse;
import com.silaev.ledger.service.LedgerService;
import com.silaev.ledger.service.TransactionHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.silaev.ledger.model.TransactionType.CREDIT;
import static com.silaev.ledger.model.TransactionType.DEBIT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/transaction")
@Validated
@RequiredArgsConstructor
public class TransactionController {
    private final LedgerService ledgerService;
    private final TransactionHistoryService transactionHistoryService;
    private final TransactionRequestToTransactionConverter transactionRequestConverter;

    @PostMapping(value = "/deposit", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BalanceResponse> deposit(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(ledgerService.deposit(transactionRequestConverter.convert(transactionRequest, DEBIT)));
    }

    @PostMapping(value = "/withdrawal", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BalanceResponse> withdrawal(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(ledgerService.withdraw(transactionRequestConverter.convert(transactionRequest, CREDIT)));
    }

    @GetMapping(value = "/history", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionHistoryResponse>> history(@Valid GetHistoryFilter historyFilter) {
        return ResponseEntity.ok(transactionHistoryService.getHistory(historyFilter));
    }
}
