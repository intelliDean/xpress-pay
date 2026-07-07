package com.api.xpress.auth_config.user.controllers;

import com.api.xpress.auth_config.security.user_services.AuthenticatedUser;
import com.api.xpress.auth_config.user.annotation.CurrentUser;
import com.api.xpress.auth_config.user.data.dtos.LoginRequest;
import com.api.xpress.auth_config.user.data.dtos.UserDTO;
import com.api.xpress.auth_config.user.services.UserService;
import com.api.xpress.customer.data.dtos.CustomerRegistrationResponse;
import com.api.xpress.customer.services.CustomerService;
import com.api.xpress.xceptions.XpressException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@AllArgsConstructor
@Tag(name = "Auth Controller")
@RequestMapping("/api/v1/auth")
public class AuthController {
     private final UserService userService;
     private final CustomerService customerService;


    @GetMapping("current")
    @Operation(summary = "Get current user logged in")
    public ResponseEntity<UserDTO> currentUser(@CurrentUser AuthenticatedUser currentUser) {
        return ResponseEntity.ok(
                userService.currentUser(currentUser)
        );
    }
    @PostMapping("/login")
    @Operation(summary = "Login")
    public void login(@RequestBody @Valid LoginRequest request) {
        throw new XpressException("Authentication failed");
    }


    @PostMapping("/logout")
    @Operation(summary = "Logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        userService.logout(request, response);
    }
    @PostMapping("verify")
    @Operation(summary = "Verify User email address")
    public ResponseEntity<CustomerRegistrationResponse> register(
            @RequestParam String token, @RequestParam String email
    ) {
        return ResponseEntity.ok(
                customerService.verifyCustomerMail(token, email)
        );
    }

    @GetMapping("refresh")
    @Operation(summary = "Get refresh token when access token expires")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        userService.refreshToken(request, response);
    }
}
