package com.silaev.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silaev.ledger.converter.TransactionRequestToTransactionConverter;
import com.silaev.ledger.model.Account;
import com.silaev.ledger.model.Transaction;
import com.silaev.ledger.request.GetHistoryFilter;
import com.silaev.ledger.request.TransactionRequest;
import com.silaev.ledger.response.AccountResponse;
import com.silaev.ledger.response.BalanceResponse;
import com.silaev.ledger.response.TransactionHistoryResponse;
import com.silaev.ledger.response.TransactionResponse;
import com.silaev.ledger.service.LedgerService;
import com.silaev.ledger.service.TransactionHistoryService;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.silaev.ledger.model.TransactionType.CREDIT;
import static com.silaev.ledger.model.TransactionType.DEBIT;
import static com.silaev.ledger.request.GetHistoryFilter.PATTERN;
import static java.util.UUID.randomUUID;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
@AutoConfigureMockMvc
class TransactionControllerTest {
    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Lisbon");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockitoBean
    private LedgerService ledgerService;

    @MockitoBean
    private TransactionRequestToTransactionConverter transactionRequestConverter;

    @MockitoBean
    private TransactionHistoryService transactionHistoryService;


    @Test
    void shouldDeposit() throws Exception {
        //GIVEN
        var accountId = randomUUID();
        var transactionRequest = TransactionRequest.builder().accountId(accountId).amount(BigDecimal.ONE).build();
        var transaction = Transaction.builder()
            .account(new Account(accountId)).amount(BigDecimal.ONE)
            .type(DEBIT)
            .timestamp(Instant.now())
            .amount(transactionRequest.amount())
            .build();
        var balanceResponseExpected = BalanceResponse.builder().account(new AccountResponse(accountId)).amount(BigDecimal.TEN).build();
        given(ledgerService.deposit(transaction)).willReturn(balanceResponseExpected);
        given(transactionRequestConverter.convert(transactionRequest, DEBIT)).willReturn(transaction);

        // WHEN
        var perform = mockMvc.perform(post("/transaction/deposit")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionRequest)));

        // THEN
        perform
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(balanceResponseExpected)));
    }

    @Test
    void shouldNotDepositWhenValidationErrors() throws Exception {
        //GIVEN
        var accountId = randomUUID();
        var amount = BigDecimal.ZERO;
        var errorMsg = "[must be greater than or equal to 0.1]";
        var transactionRequest = TransactionRequest.builder().accountId(accountId).amount(amount).build();
        var transaction = Transaction.builder()
            .account(new Account(accountId))
            .type(DEBIT)
            .timestamp(Instant.now())
            .amount(transactionRequest.amount())
            .build();
        var balanceResponse = BalanceResponse.builder().account(new AccountResponse(accountId)).amount(BigDecimal.TEN).build();
        given(ledgerService.deposit(transaction))
            .willReturn(balanceResponse);
        given(transactionRequestConverter.convert(transactionRequest, CREDIT)).willReturn(transaction);

        // WHEN
        var perform = mockMvc.perform(post("/transaction/deposit")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionRequest)));

        // THEN
        perform
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message", StringContains.containsString(errorMsg)));
    }

    @Test
    void shouldWithdrawal() throws Exception {
        //GIVEN
        var accountId = randomUUID();
        var transactionRequest = TransactionRequest.builder().accountId(accountId).amount(BigDecimal.ONE).build();
        var transaction = Transaction.builder()
            .account(new Account(accountId)).amount(BigDecimal.ONE)
            .type(CREDIT)
            .timestamp(Instant.now())
            .amount(transactionRequest.amount())
            .build();
        var balanceResponseExpected = BalanceResponse.builder().account(new AccountResponse(accountId)).amount(BigDecimal.TEN).build();
        given(ledgerService.withdraw(transaction)).willReturn(balanceResponseExpected);
        given(transactionRequestConverter.convert(transactionRequest, CREDIT)).willReturn(transaction);

        // WHEN
        var perform = mockMvc.perform(post("/transaction/withdrawal")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionRequest)));

        // THEN
        perform
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(balanceResponseExpected)));
    }

    @Test
    void shouldNotWithdrawWhenValidationErrors() throws Exception {
        //GIVEN
        var accountId = randomUUID();
        var amount = BigDecimal.ZERO;
        var errorMsg = "[must be greater than or equal to 0.1]";
        var transactionRequest = TransactionRequest.builder().accountId(accountId).amount(amount).build();
        var transaction = Transaction.builder()
            .account(new Account(accountId)).amount(BigDecimal.ONE)
            .type(CREDIT)
            .timestamp(Instant.now())
            .amount(transactionRequest.amount())
            .build();
        var balanceResponse = BalanceResponse.builder().account(new AccountResponse(accountId)).amount(BigDecimal.TEN).build();
        given(ledgerService.withdraw(transaction))
            .willReturn(balanceResponse);
        given(transactionRequestConverter.convert(transactionRequest, CREDIT)).willReturn(transaction);

        // WHEN
        var perform = mockMvc.perform(post("/transaction/withdrawal")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionRequest)));

        // THEN
        perform
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message", StringContains.containsString(errorMsg)));
    }

    @Test
    void shouldGetHistory() throws Exception {
        //GIVEN
        var accountId = randomUUID();
        var accountResponse = new AccountResponse(accountId);
        var from = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        var to = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
        var txHistoryResponse = getTxHistoryResponse(accountResponse, from.toInstant(ZoneOffset.UTC), to.toInstant(ZoneOffset.UTC));
        given(transactionHistoryService.getHistory(new GetHistoryFilter(accountId, from, to, ZONE_ID))).willReturn(txHistoryResponse);

        // WHEN
        var perform = mockMvc.perform(get("/transaction/history")
            .param("accountId", accountId.toString())
            .param("from", from.format(FORMATTER))
            .param("to", to.format(FORMATTER))
            .param("zoneId", ZONE_ID.toString())
            .accept(APPLICATION_JSON));

        // THEN
        perform.andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(txHistoryResponse)));
    }

    @Test
    void shouldNotGetHistoryWhenFromDateIsNotLessThanToDate() throws Exception {
        //GIVEN
        var accountId = randomUUID();
        var account = new AccountResponse(accountId);
        var to = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        var from = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
        var txHistoryResponse = getTxHistoryResponse(account, from.toInstant(ZoneOffset.UTC), to.toInstant(ZoneOffset.UTC));
        given(transactionHistoryService.getHistory(new GetHistoryFilter(accountId, from, to, ZONE_ID))).willReturn(txHistoryResponse);
        var errorMsg = "[The 'from' date must be less than or equal to the 'to' date]";

        // WHEN
        var perform = mockMvc.perform(get("/transaction/history")
            .param("accountId", accountId.toString())
            .param("from", from.format(FORMATTER))
            .param("to", to.format(FORMATTER))
            .accept(APPLICATION_JSON));

        // THEN
        perform.andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message", StringContains.containsString(errorMsg)));
    }

    private List<TransactionHistoryResponse> getTxHistoryResponse(AccountResponse account, Instant from, Instant to) {
        return List.of(TransactionHistoryResponse.builder()
                .timestamp(from.atZone(ZONE_ID))
                .transactions(
                    List.of(
                        TransactionResponse.builder()
                            .account(account)
                            .type(DEBIT)
                            .amount(new BigDecimal("10.20"))
                            .build(),
                        TransactionResponse.builder()
                            .account(account)
                            .type(CREDIT)
                            .amount(new BigDecimal("2.50"))
                            .build()
                    )
                ).build(),
            TransactionHistoryResponse.builder()
                .timestamp(to.atZone(ZONE_ID))
                .transactions(
                    List.of(
                        TransactionResponse.builder()
                            .account(account)
                            .type(DEBIT)
                            .amount(new BigDecimal("120"))
                            .build()
                    )
                ).build(),
            TransactionHistoryResponse.builder()
                .timestamp(from.atZone(ZONE_ID))
                .transactions(
                    List.of(
                        TransactionResponse.builder()
                            .account(account)
                            .type(DEBIT)
                            .amount(new BigDecimal("10.20"))
                            .build(),
                        TransactionResponse.builder()
                            .account(account)
                            .type(CREDIT)
                            .amount(new BigDecimal("2.50"))
                            .build()
                    )
                ).build()
        );
    }
}
