package com.api.xpress.customer.data.dtos;

import lombok.*;

@Builder
public record CustomerRegisterRequest (

    String fullName,

    String emailAddress,

    String password,

    String phoneNumber
){}
