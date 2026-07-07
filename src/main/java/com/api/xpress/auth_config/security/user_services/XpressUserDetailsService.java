package com.api.xpress.auth_config.security.user_services;

import com.api.xpress.auth_config.user.data.models.User;
import com.api.xpress.auth_config.user.services.UserService;
import com.api.xpress.xceptions.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class XpressUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public @NonNull UserDetails loadUserByUsername(
            final @NonNull String emailAddress) throws UsernameNotFoundException {
        try {

            User user = userService.findUserByEmail(emailAddress);

            return new AuthenticatedUser(user);

        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException("Invalid email or password", e);
        }
    }
}
