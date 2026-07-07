package com.api.xpress.auth_config.user.services;

import com.api.xpress.auth_config.security.auth_utils.JwtService;
import com.api.xpress.auth_config.user.data.enums.Role;
import com.api.xpress.auth_config.user.data.models.User;
import com.api.xpress.auth_config.user.data.repositories.UserRepository;
import com.api.xpress.auth_config.user.mappers.UserMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private final XpressTokenService xpressTokenService = mock(XpressTokenServiceImpl.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final UserMapper userMapper = mock(UserMapper.class);

    private final UserService userService =
            new UserServiceImpl(xpressTokenService, userRepository, jwtService, userMapper);

    User user = User.builder()
            .roles(Collections.singleton(Role.CUSTOMER))
            .enabled(true)
            .password("Password")
            .emailAddress("email@gmail.com")
            .fullName("Full Name")
            .build();


    @Test
    void findUserByEmail() {
        String email = "email@gmail.com";
        when(userRepository.findUserByEmailAddress(email))
                .thenReturn(Optional.of(user));
        User foundUser = userService.findUserByEmail(email);
        assertThat(foundUser).isNotNull().isInstanceOf(User.class);
        assertThat(foundUser.getEmailAddress()).isEqualTo("email@gmail.com");
    }
    @Test
    void itThrowsExceptionIfUserNotFound() {
        String email = "email@gmail.com";
        when(userRepository.findUserByEmailAddress(email))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                ()-> {
                     userService.findUserByEmail(email);
                }
        ).isInstanceOf(RuntimeException.class).hasMessage("User not found");
    }

    @Test
    void deleteUser() {
        doNothing().when(userRepository).delete(user);
    }

    @Test
    void getAllUsers() {
        List<User> users = List.of(mock(User.class), mock(User.class));
        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.getAllUsers();
        assertThat(foundUsers).isInstanceOf(List.class);
        assertThat(foundUsers.size()).isEqualTo(2);
    }
}