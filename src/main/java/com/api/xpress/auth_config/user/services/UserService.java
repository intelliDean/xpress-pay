package com.api.xpress.auth_config.user.services;

import com.api.xpress.auth_config.security.user_services.AuthenticatedUser;
import com.api.xpress.auth_config.user.data.dtos.UserDTO;
import com.api.xpress.auth_config.user.data.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface UserService {

    User findUserByEmail(String email);

    boolean userExist(String email);

    void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;

    User getCurrentUser();

    UserDTO currentUser(AuthenticatedUser currentUser);

    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;
    void saveUser(User user);
    void  deleteUser(User user);
    List<User> getAllUsers();

    User findUserById(Long userId);
}
