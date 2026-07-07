package com.api.xpress.airtime.service;

import com.api.xpress.airtime.data.models.Biller;
import com.api.xpress.xpress_utils.XpressUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.api.xpress.airtime.data.dtos.Details;
import com.api.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.api.xpress.airtime.data.dtos.XpressAPIRequestDTO;
import com.api.xpress.airtime.data.models.AirtimePurchase;
import com.api.xpress.airtime.data.models.Status;
import com.api.xpress.airtime.data.repository.AirtimePurchaseRepository;
import com.api.xpress.auth_config.user.data.models.User;
import com.api.xpress.auth_config.user.services.UserService;
import com.api.xpress.transaction.data.model.Transaction;
import com.api.xpress.transaction.data.model.TransactionType;
import com.api.xpress.transaction.services.TransactionService;
import com.api.xpress.xceptions.XpressException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AirtimePurchaseServiceImpl implements AirtimePurchaseService {
    @Value("${xpress.key.private}")
    private String privateKey;

    @Value("${xpress.api.url}")
    private String xpressUrl;

    @Value("${xpress.key.public}")
    private String publicKey;

    private final AirtimePurchaseRepository airtimePurchaseRepository;
    private final TransactionService transactionService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public AirtimePurchaseServiceImpl(
            AirtimePurchaseRepository airtimePurchaseRepository,
            TransactionService transactionService,
            UserService userService,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.airtimePurchaseRepository = airtimePurchaseRepository;
        this.objectMapper = objectMapper;
        this.transactionService = transactionService;
        this.userService = userService;
        this.webClient = webClientBuilder
                .baseUrl(xpressUrl)
                .defaultHeader("Authorization", publicKey)
                .defaultHeader("channel", "api")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    @Override
    public AirtimePurchaseResponse buyAirtime(PurchaseAirtimeRequestDTO requestDTO) {

        User user = userService.findUserById(requestDTO.userId());
        BigDecimal amount = requestDTO.amount();

        AirtimePurchase savedAirtimePurchase = airtimePurchaseRepository.save(
                buildAirtimePurchase(
                        requestDTO.phoneNumber(),
                        user,
                        amount,
                        uniqueCode(requestDTO.phoneNumber())
                )
        );

        XpressAPIRequestDTO xpressAPIRequestDTO = buildXpressAPIRequestDTO(savedAirtimePurchase);

        saveTransaction(user, amount);

        return callToXpressAPI(xpressAPIRequestDTO);
    }

    private AirtimePurchase buildAirtimePurchase(
            String phoneNumber,
            User user,
            BigDecimal amount,
            String uniqueCode
    ) {
        return AirtimePurchase.builder()
                .phoneNumber(phoneNumber)
                .amount(amount)
                .uniqueCode(uniqueCode)
                .user(user)
                .transactionTime(LocalDateTime.now())
                .status(Status.PENDING)
                .build();
    }

    private XpressAPIRequestDTO buildXpressAPIRequestDTO(AirtimePurchase savedAirtimePurchase) {

        return XpressAPIRequestDTO.builder()
                .requestId(savedAirtimePurchase.getId())
                .uniqueCode(savedAirtimePurchase.getUniqueCode())
                .details(Details.builder()
                        .amount(savedAirtimePurchase.getAmount())
                        .phoneNumber(savedAirtimePurchase.getPhoneNumber())
                        .build()
                ).build();
    }


    private String uniqueCode(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber) || phoneNumber.length() < 4) {
            throw new XpressException("Invalid phone number format");
        }

        return Biller.fromPrefix(phoneNumber.substring(0, 4)).getUniqueCode();
    }

    private void saveTransaction(User user, BigDecimal amount) {

        transactionService.saveTransaction(
                Transaction.builder()
                        .user(user)
                        .transactionType(TransactionType.BUY_AIRTIME)
                        .amount(amount)
                        .build()
        );
    }

    private AirtimePurchaseResponse callToXpressAPI(XpressAPIRequestDTO xpressAPIRequestDTO) {

        try {
            String jsonString = objectMapper.writeValueAsString(xpressAPIRequestDTO);
            String paymentHash = XpressUtils.calculateHMAC512(jsonString, privateKey);

            return webClient.post()
                    .header("PaymentHash", paymentHash)
                    .bodyValue(xpressAPIRequestDTO)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new XpressException(
                                            "Xpress API error: " + body))
                    )
                    .bodyToMono(AirtimePurchaseResponse.class)
                    .block();

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Xpress request: {}", e.getMessage());
            throw new XpressException("Failed to process payment request");
        }
    }

}