package com.api.xpress.auth_config.user.services;

import com.api.xpress.auth_config.user.mappers.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.xpress.auth_config.security.auth_utils.JwtService;
import com.api.xpress.auth_config.security.auth_utils.XpressAuthToken;
import com.api.xpress.auth_config.security.user_services.AuthenticatedUser;
import com.api.xpress.auth_config.user.data.dtos.UserDTO;
import com.api.xpress.auth_config.user.data.models.User;
import com.api.xpress.auth_config.user.data.models.XpressToken;
import com.api.xpress.auth_config.user.data.repositories.UserRepository;
import com.api.xpress.xceptions.UserNotAuthorizedException;
import com.api.xpress.xceptions.UserNotFoundException;
import com.api.xpress.xceptions.XpressException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.api.xpress.xpress_utils.XpressConstants.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final XpressTokenService xpressTokenService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public User findUserByEmail(String email) {        //this method finds the user from the database by email
        return userRepository.findUserByEmailAddress(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public boolean userExist(String email) {
        return userRepository.existsByEmailAddress(email);
    }

    @Override    //this methods logs a user out of the platform rendering the jwt token useless
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String header = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(header) && StringUtils.startsWithIgnoreCase(header, BEARER)) {

            //the access token is extracted from the header
            final String accessToken = header.substring(BEARER.length());

            //the token is subjected to validation
            if (jwtService.isValid(accessToken)) {
                xpressTokenService.revokeToken(accessToken);
                //if token is valid, then the security context holder is cleared of the user with the token
                SecurityContextHolder.clearContext();

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), "User Logout successfully");
            }
        }
    }

    @Override    //this gets the current user logged in at a particular time instead of using id to find user
    public User getCurrentUser() {
        try {
            final AuthenticatedUser authenticatedUser =
                    (AuthenticatedUser) SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getPrincipal();
            return authenticatedUser.user();
        } catch (Exception ex) {
            throw new UserNotAuthorizedException();
        }
    }

    @Override    //the user object is mapped to a dto and returned instead of the real user object
    public UserDTO currentUser(AuthenticatedUser currentUser) {
        return userMapper.toDTO(findUserByEmail(currentUser.getUsername()));
    }

    @Override    //this method regenerates access token when the available ones expire
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, BEARER)) {
            return;
        }
        //the refresh token is extracted from the header
        final String refreshToken = authHeader.substring(BEARER.length());

        //the refresh token ois validated
        if (jwtService.isValid(refreshToken)) {
            final String email = jwtService.extractUsernameFromToken(refreshToken);

            //email is extracted from the refresh token
            if (StringUtils.hasText(email)) {
                //email is used to load user
                final User user = findUserByEmail(email);

                //user details are used to generate a new access token maintaining the old refresh token
                final String accessToken = jwtService.generateAccessToken(user);
                final XpressAuthToken newLoginTokens = XpressAuthToken.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();

                //refresh token is used to retrieve the token object from the database
                final XpressToken xpressToken = xpressTokenService.getValidTokenByAnyToken(refreshToken)
                                .orElseThrow(() -> new XpressException("Token could not be found"));

                xpressToken.setAccessToken(accessToken);    //access token is updated
                xpressTokenService.saveToken(xpressToken);    //and then saved

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), newLoginTokens);
            }
        }
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
