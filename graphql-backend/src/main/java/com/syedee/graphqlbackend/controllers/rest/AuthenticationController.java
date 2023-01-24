package com.syedee.graphqlbackend.controllers.rest;

import com.syedee.graphqlbackend.config.CurrentUser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.el.MethodNotFoundException;
import javax.servlet.http.Cookie;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "/api/rest/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    public AuthenticationController(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("signup")
    public ResponseEntity signUp(HttpServletResponse response) {
        throw new UnsupportedOperationException();
    }

    @PostMapping("login")
    public ResponseEntity logIn(HttpServletResponse response) {
        addAuthCookie(response);
        return ResponseEntity.ok("");
    }

    @GetMapping("logout")
    public ResponseEntity logOut(HttpServletResponse response) {
        throw new UnsupportedOperationException();
    }

    private void addAuthCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("baler_cookie", "baler_value")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .maxAge(12313)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private ResponseEntity performBasicAuthLogin(BasicAuthRequest basicAuthRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            basicAuthRequest.getUserName(),
                            basicAuthRequest.getPassword()
                    )
            );
            if (authentication.isAuthenticated()) {
                var currentUserName = ((CurrentUser) authentication.getPrincipal()).getUsername();
                var currentUserId = ((CurrentUser) authentication.getPrincipal()).getId();
                return ResponseEntity
                        .accepted()
                        .headers(hs -> {
                            hs.set("X-CurrentUserName", currentUserName);
                            hs.set("X-CurrentUserId", currentUserId.toString());
                            hs.setBasicAuth(basicAuthRequest.getUserName(), basicAuthRequest.getPassword());
                        })
                        .build();
            }
            return ResponseEntity
                    .status(UNAUTHORIZED)
                    .build();
        } catch (AuthenticationException authenticationException) {
            return ResponseEntity
                    .status(UNAUTHORIZED)
                    .build();
        }
    }
}
