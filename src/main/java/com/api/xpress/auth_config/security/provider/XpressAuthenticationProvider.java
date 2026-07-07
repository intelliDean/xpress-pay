package com.api.xpress.auth_config.security.provider;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class XpressAuthenticationProvider implements AuthenticationProvider {

    //there are many authentication providers in spring security
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override	//the authentication provider is the checked the supplied credentials against what's in the database
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //email and password are extracted from the authentication object
        final String requestEmail = Objects.requireNonNull(authentication.getPrincipal()).toString();
        final String requestPassword = Objects.requireNonNull(authentication.getCredentials()).toString();

        //
        final UserDetails userDetails = userDetailsService.loadUserByUsername(requestEmail);

        final String email = userDetails.getUsername();
        final String password = userDetails.getPassword();
        //the supplied password and email is checked against the password and email in the database
        //authentication is completed here
        if (passwordEncoder.matches(requestPassword, password)) {
            return new UsernamePasswordAuthenticationToken(
                    email,
                    password,
                    userDetails.getAuthorities()
            );
        }
        throw new BadCredentialsException("Incorrect username or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
