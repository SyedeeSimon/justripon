package com.syedee.graphqlbackend.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CurrentUser extends User {
    private final Integer id;

    public CurrentUser(final Integer id, final String username, final String password, final Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public static CurrentUser of(Integer id, String username, String password, String role) {
        return new CurrentUser(id, username, password, AuthorityUtils.createAuthorityList(role));
    }

    public Integer getId() {
        return id;
    }
}
