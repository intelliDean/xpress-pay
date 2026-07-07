package com.api.xpress.auth_config.security.filters;

import com.api.xpress.auth_config.user.data.dtos.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.xpress.auth_config.security.auth_utils.JwtService;
import com.api.xpress.auth_config.security.auth_utils.XpressAuthToken;
import com.api.xpress.auth_config.security.user_services.AuthenticatedUser;
import com.api.xpress.auth_config.user.data.models.XpressToken;
import com.api.xpress.auth_config.user.services.XpressTokenService;
import com.api.xpress.xceptions.XpressException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class XpressAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    //this is the first filter that intercepts the authentication request from the user
    private final AuthenticationManager authenticationManager;
    private final XpressTokenService xpressTokenService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;

    @Override        //this method attempts to authenticate the user
    public @Nullable Authentication attemptAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response) throws AuthenticationException {
        try {
            //user credentials are gotten from the request
            final LoginRequest user = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            //an authentication object is created with the user credentials but not authenticated
            final Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.emailAddress(),
                    user.password()
            );
            //the authentication object is sent to the authentication manager, who then sends it to a provider
            final Authentication authenticationResult = authenticationManager.authenticate(authentication);
            //if the authentication is successful then the object will not be null
            //the result of the authentication is then set into the security context holder
            SecurityContextHolder.getContext().setAuthentication(authenticationResult);
            return SecurityContextHolder.getContext().getAuthentication();
        } catch (IOException ex) {
            throw new XpressException("Authentication failed");
        }
    }

    @Override    //if the authentication is successful, this method is called
    protected void successfulAuthentication(
            @NonNull HttpServletRequest request,
            HttpServletResponse response,
            @NonNull FilterChain chain,
            Authentication authResult) throws IOException {

        //principal is the user username
        final String email = Objects.requireNonNull(authResult.getPrincipal()).toString();

        //user is loaded from the database using the email
        final AuthenticatedUser authenticatedUser = (AuthenticatedUser) userDetailsService.loadUserByUsername(email);

        //access and refresh token are generated after the authentication is successful
        final XpressAuthToken tokens = jwtService.generateTokens(authenticatedUser.user());

        //xpress token is an in house token created to internally validate the jwt token
        //against logging out from the backend
        xpressTokenService.saveToken(XpressToken.builder()
                .user(authenticatedUser.user())
                .refreshToken(tokens.refreshToken())
                .accessToken(tokens.accessToken())
                .build()
        );

        //the token is return to the user
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), tokens);
    }
}
