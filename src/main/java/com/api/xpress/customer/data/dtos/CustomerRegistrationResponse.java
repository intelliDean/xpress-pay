package com.api.xpress.customer.data.dtos;

import com.api.xpress.auth_config.security.auth_utils.XpressAuthToken;
import lombok.*;

@Builder
public record CustomerRegistrationResponse(

        String message,

        XpressAuthToken xpressAuthToken
) {
}
