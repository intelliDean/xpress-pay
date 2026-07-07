package com.api.xpress.airtime.data.dtos;

import lombok.*;

@Builder
public record AirtimePurchaseResponse(

        String referenceId,

        String requestId,

        String responseCode,

        String responseMessage,

        Object data
) {
}
