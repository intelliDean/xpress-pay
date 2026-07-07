package com.api.xpress.airtime.data.dtos;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record PurchaseAirtimeRequestDTO (

    Long userId,

    String phoneNumber,

    BigDecimal amount
){}
