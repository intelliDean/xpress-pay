package com.api.xpress.auth_config.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.xpress.auth_config.user.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @Test
    void currentUser() {
    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }

    @Test
    void refreshToken() {
    }
}