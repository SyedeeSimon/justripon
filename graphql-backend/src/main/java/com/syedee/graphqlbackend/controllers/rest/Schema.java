package com.syedee.graphqlbackend.controllers.rest;

import java.util.Objects;

import static com.syedee.graphqlbackend.config.Constants.ANONYMOUS_USER_NAME;
import static com.syedee.graphqlbackend.config.Constants.ANONYMOUS_USER_PASSWORD;

class BasicAuthRequest {

    public static final BasicAuthRequest ANONYMOUS_AUTH_REQUEST = BasicAuthRequest.of(ANONYMOUS_USER_NAME, ANONYMOUS_USER_PASSWORD);
    private final String userName;
    private final String password;

    public BasicAuthRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public static BasicAuthRequest of(String userName, String password) {
        return new BasicAuthRequest(userName, password);
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isValid() {
        return Objects.nonNull(getUserName()) && !getUserName().isBlank()
                && Objects.nonNull(getPassword()) && !getPassword().isBlank();

    }
}