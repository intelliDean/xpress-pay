package com.api.xpress.auth_config.security.auth_utils;

import lombok.Getter;

@Getter
public class WhiteList {

    //these are the urls that needs no authentication to be accessed
    public static String[] authenticationNotNeeded() {
        return new String[]{
                "/api/v1/auth/**",
                "/api/v1/customer/register",
        };
    }

    //this is giving swagger free access. swagger is for documentation
    public static String[] swagger() {
        return new String[]{
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**"
        };
    }
}
