package com.api.xpress.airtime.data.dtos;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record Details (

    String phoneNumber,

    BigDecimal amount
){}