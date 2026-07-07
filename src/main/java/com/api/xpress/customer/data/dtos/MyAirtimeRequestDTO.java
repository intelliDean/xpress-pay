package com.api.xpress.customer.data.dtos;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record MyAirtimeRequestDTO(
        BigDecimal amount
) {}
