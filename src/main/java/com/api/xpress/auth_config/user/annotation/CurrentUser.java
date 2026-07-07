package com.api.xpress.auth_config.user.annotation;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER) // ElementType.TYPE
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(errorOnInvalidType = true)
public @interface CurrentUser {}