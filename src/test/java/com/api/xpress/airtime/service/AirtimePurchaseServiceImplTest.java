package com.api.xpress.airtime.service;

import com.api.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.api.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.api.xpress.airtime.data.models.AirtimePurchase;
import com.api.xpress.airtime.data.models.Status;
import com.api.xpress.airtime.data.repository.AirtimePurchaseRepository;
import com.api.xpress.auth_config.user.data.enums.Role;
import com.api.xpress.auth_config.user.data.models.User;
import com.api.xpress.auth_config.user.services.UserService;
import com.api.xpress.transaction.data.model.Transaction;
import com.api.xpress.transaction.data.model.TransactionType;
import com.api.xpress.transaction.services.TransactionService;
import com.api.xpress.transaction.services.TransactionServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import com.api.xpress.airtime.data.models.Biller;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AirtimePurchaseServiceImplTest {
    private final AirtimePurchaseRepository airtimePurchaseRepository =
            mock(AirtimePurchaseRepository.class);
    private final TransactionService transactionService =
            mock(TransactionServiceImpl.class);
    private final UserService userService =
            mock(UserService.class);
    private final WebClient.Builder webClient = mock(WebClient.Builder.class, org.mockito.Answers.RETURNS_SELF);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebClient mockWebClient;
    private AirtimePurchaseService airtimePurchaseService;

    @BeforeEach
    void setUp() {
        Biller.MTN.setUniqueCode("MTN_24207");
        Biller.GLO.setUniqueCode("GLO_30387");
        Biller.AIRTEL.setUniqueCode("AIRTEL_22689");
        Biller.ETISALAT.setUniqueCode("9MOBILE_69358");

        mockWebClient = mock(WebClient.class, org.mockito.Answers.RETURNS_DEEP_STUBS);
        when(webClient.build()).thenReturn(mockWebClient);

        airtimePurchaseService = new AirtimePurchaseServiceImpl(
                airtimePurchaseRepository,
                transactionService,
                userService,
                objectMapper,
                webClient
        );

        org.springframework.test.util.ReflectionTestUtils.setField(airtimePurchaseService, "privateKey", "test_private_key");
        org.springframework.test.util.ReflectionTestUtils.setField(airtimePurchaseService, "xpressUrl", "http://localhost");
        org.springframework.test.util.ReflectionTestUtils.setField(airtimePurchaseService, "publicKey", "test_public_key");
        org.springframework.test.util.ReflectionTestUtils.setField(airtimePurchaseService, "url", "http://localhost");
    }

    @Test
    void buyAirtime() throws IOException {
        User user = User.builder()
                .id(1L)
                .roles(Collections.singleton(Role.CUSTOMER))
                .enabled(true)
                .password("Password")
                .emailAddress("email@gmail.com")
                .fullName("Full Name")
                .build();

        AirtimePurchase airtimePurchase = AirtimePurchase.builder()
                .id("String")
                .phoneNumber("08033333333")
                .amount(BigDecimal.valueOf(2300))
                .uniqueCode("MTN_24207")
                .user(user)
                .transactionTime(LocalDateTime.now())
                .status(Status.PENDING)
                .build();
        AirtimePurchase savedAirtimePurchase = AirtimePurchase.builder()
                .id(airtimePurchase.getId())
                .phoneNumber(airtimePurchase.getPhoneNumber())
                .amount(airtimePurchase.getAmount())
                .uniqueCode(airtimePurchase.getUniqueCode())
                .user(airtimePurchase.getUser())
                .transactionTime(airtimePurchase.getTransactionTime())
                .status(airtimePurchase.getStatus())
                .build();

        Transaction transaction = Transaction.builder()
                .user(mock(User.class))
                .transactionType(TransactionType.BUY_AIRTIME)
                .amount(BigDecimal.valueOf(2300))
                .transactionTime(airtimePurchase.getTransactionTime())
                .build();
        ;
        when(userService.findUserById(1L)).thenReturn(user);
        when(airtimePurchaseRepository.save(any(AirtimePurchase.class)))
                .thenReturn(savedAirtimePurchase);
        doNothing().when(transactionService).saveTransaction(any(Transaction.class));

        AirtimePurchaseResponse mockResponse = mock(AirtimePurchaseResponse.class);
        when(mockWebClient.post()
                .header(anyString(), anyString())
                .bodyValue(any())
                .retrieve()
                .onStatus(any(), any())
                .bodyToMono(any(Class.class))
                .block())
                .thenReturn(mockResponse);

        AirtimePurchaseResponse response = airtimePurchaseService.buyAirtime(
                PurchaseAirtimeRequestDTO.builder()
                        .userId(1L)
                        .phoneNumber("08095729090")
                        .amount(BigDecimal.valueOf(1200))
                        .build()
        );
        assertThat(response).isNotNull().isInstanceOf(AirtimePurchaseResponse.class);

    }
}