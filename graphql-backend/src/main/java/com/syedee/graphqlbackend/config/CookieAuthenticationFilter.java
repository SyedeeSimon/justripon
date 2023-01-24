package com.syedee.graphqlbackend.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

public class CookieAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHENTICATION_COOKIE_NAME = "auth_token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] clientCookies = Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]);
        Optional<Cookie> maybeAuthCookie = Stream.of(clientCookies).filter(cookie -> cookie.getName().equals(AUTHENTICATION_COOKIE_NAME)).findAny();
        maybeAuthCookie.ifPresent(cookie -> {
            SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(cookie.getValue(), null));
        });
        filterChain.doFilter(request, response);
    }
}
