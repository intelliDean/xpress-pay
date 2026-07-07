package com.api.xpress.admin;

import com.api.xpress.auth_config.user.data.enums.Role;
import com.api.xpress.auth_config.user.data.models.User;
import com.api.xpress.auth_config.user.services.UserService;
import com.api.xpress.xceptions.XpressException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SuperAdminService {
    @Value("${admin.full_name}")
    private String fullName;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void createSuperAdmin() {
        try {
            User admin = userService.findUserByEmail(email);
            if (admin != null) {
                return;
            }
        } catch (com.api.xpress.xceptions.UserNotFoundException e) {
            // Admin does not exist yet, proceed to create
        }

        userService.saveUser(User.builder()
                .fullName(fullName)
                .emailAddress(email)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .roles(Collections.singleton(Role.SUPER_ADMIN))
                .build());
    }


    @PreDestroy
    public void deleteSuperAdmin() {
        try {
            User admin = userService.findUserByEmail(email);
            if (admin != null) {
                userService.deleteUser(admin);
            }
        } catch (Exception e) {
            // Ignore during destroy
        }
    }
}
