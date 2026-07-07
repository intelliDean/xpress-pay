package com.api.xpress.xceptions;

import org.springframework.http.HttpStatus;

public class UserNotAuthorizedException extends XpressException {

    public UserNotAuthorizedException() {
        this("Unauthorized");
    }

    public UserNotAuthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
