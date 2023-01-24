package com.syedee.graphqlbackend.config;

import com.syedee.graphqlbackend.database.PostgresQueryService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static com.syedee.graphqlbackend.config.Constants.ANONYMOUS_USER_NAME;
import static com.syedee.graphqlbackend.config.Constants.ANONYMOUS_USER_PASSWORD;

@Component
public class DefaultUserDetailsService implements UserDetailsService {

    private static final CurrentUser ANONYMOUS_USER_DETAILS = CurrentUser.of(0, ANONYMOUS_USER_NAME, ANONYMOUS_USER_PASSWORD, Role.VISITOR.toString());

    private final PostgresQueryService postgresQueryService;

    public DefaultUserDetailsService(final PostgresQueryService postgresQueryService) {
        this.postgresQueryService = postgresQueryService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return postgresQueryService.findUserByName(username)
                .map(user -> CurrentUser.of(user.getId(), user.getName(), user.getPassword(), user.getRole())
                )
                .orElse(ANONYMOUS_USER_DETAILS);
    }
}
