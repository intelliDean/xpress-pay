package com.api.xpress.auth_config.security.filters;

import com.api.xpress.auth_config.security.auth_utils.JwtService;
import com.api.xpress.auth_config.user.services.XpressTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.api.xpress.xpress_utils.XpressConstants.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@AllArgsConstructor
public class XpressAuthorizationFilter extends OncePerRequestFilter {
    //this class is responsible for the authorization of the authenticated user
    private final UserDetailsService userDetailsService;
    private final XpressTokenService xpressTokenService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        //request header is extracted
        final String authHeader = request.getHeader(AUTHORIZATION);
        //the header is check if not null and starts with the word "Bearer "
        if (StringUtils.hasText(authHeader) && StringUtils.startsWithIgnoreCase(authHeader, BEARER)) {
            //jwt token is extracted from the header
            final String accessToken = authHeader.substring(BEARER.length());

            //this line is doing double validation
            //1. validates the authenticity of the jwt token and it's expiration
            //2. validates if the token is revoked, that is, the user has logged out rendering the token useless
            if (jwtService.isValid(accessToken) && xpressTokenService.isTokenValid(accessToken)) {
                //the token is then used to get the subject(email/username) from the token
                final String email = jwtService.extractUsernameFromToken(accessToken);

                //this line checks if the subject/email is not null and that the security context holder is empty,
                //meaning the user is not already logged on another device
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    //the user details are loaded from the db using the email
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    if (userDetails.isEnabled()) {
                        final UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                     //the user and its details are saved in the security context holder
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
