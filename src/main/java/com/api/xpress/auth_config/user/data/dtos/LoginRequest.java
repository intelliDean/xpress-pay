package com.api.xpress.auth_config.user.data.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.api.xpress.xpress_utils.XpressConstants.NOT_BLANK;
import static com.api.xpress.xpress_utils.XpressConstants.NOT_NULL;

@Builder
public record LoginRequest (

    @NotNull(message = NOT_NULL)
    @NotBlank(message = NOT_BLANK)
    String emailAddress,

    @NotNull(message = NOT_NULL)
    @NotBlank(message = NOT_BLANK)
    String password
){}
