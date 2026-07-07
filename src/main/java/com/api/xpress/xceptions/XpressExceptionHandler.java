package com.api.xpress.xceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class XpressExceptionHandler {

    @ExceptionHandler(XpressException.class)
    public ResponseEntity<XpressExceptionResponse> handleException(
            XpressException exception
    ) {
        return ResponseEntity.badRequest()
                .body(
                        XpressExceptionResponse.builder()
                                .message(exception.getMessage())
                                .status(exception.getStatus())
                                .build()
                );
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseEntity<XpressExceptionResponse> handleException(
            UserNotAuthorizedException exception
    ) {
        return ResponseEntity.badRequest()
                .body(
                        XpressExceptionResponse.builder()
                                .message(exception.getMessage())
                                .status(exception.getStatus())
                                .build()
                );
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<XpressExceptionResponse> handleException(
            UserNotFoundException exception
    ) {
        return ResponseEntity.badRequest()
                .body(
                        XpressExceptionResponse.builder()
                                .message(exception.getMessage())
                                .status(exception.getStatus())
                                .build()
                );
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<XpressExceptionResponse> handleException(
            BadCredentialsException exception
    ) {
        return ResponseEntity.badRequest()
                .body(
                        XpressExceptionResponse.builder()
                                .message(exception.getMessage())
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
    }
}
