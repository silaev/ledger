package com.silaev.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silaev.ledger.response.AccountResponse;
import com.silaev.ledger.response.BalanceResponse;
import com.silaev.ledger.service.LedgerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static java.util.UUID.randomUUID;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BalanceController.class)
@AutoConfigureMockMvc
class BalanceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LedgerService ledgerService;

    @Test
    void shouldGetBalance() throws Exception {
        //GIVEN
        var accountId = randomUUID();
        var balanceResponseExpected = BalanceResponse.builder().account(new AccountResponse(accountId)).amount(BigDecimal.TEN).build();
        given(ledgerService.getBalance(accountId)).willReturn(balanceResponseExpected);

        // WHEN
        var perform = mockMvc.perform(get("/balance/" + accountId).accept(APPLICATION_JSON));

        // THEN
        perform
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(balanceResponseExpected)));
    }
}
