package com.api.xpress.auth_config.security.auth_utils;

import lombok.*;

@Builder
public record XpressAuthToken (
    //this is the class that access and refresh token are being mapped to at the point of logging in
    String accessToken,

    String refreshToken
){}
