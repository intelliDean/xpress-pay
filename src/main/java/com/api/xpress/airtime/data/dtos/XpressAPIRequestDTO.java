package com.api.xpress.airtime.data.dtos;

import lombok.*;


@Builder
public record XpressAPIRequestDTO (

    String requestId,

    String uniqueCode,

    Details details
){}
