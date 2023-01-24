package com.syedee.graphqlbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsernamePasswordAuthenticationFilter extends OncePerRequestFilter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO: Read the login URL from application properties through class constructor and save in a variable
        if (request.getServletPath().equals("/rest/auth/login") && HttpMethod.POST.matches(request.getMethod())) {
            LoginFormData loginFormData = objectMapper.readValue(request.getInputStream(), LoginFormData.class);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(loginFormData.getUsername(), loginFormData.getPassword())
            );
        }
        filterChain.doFilter(request, response);
    }
}
