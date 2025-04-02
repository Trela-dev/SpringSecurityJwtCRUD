package com.github.treladev.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Custom authentication filter for handling login requests and generating JWT tokens.
 *
 * - Parses JSON login requests (username & password).
 * - Authenticates users using Spring Security's AuthenticationManager.
 * - On successful authentication, generates a JWT token and adds it to the response header.
 */
@Component
public class JWTCustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JWTCustomUsernamePasswordAuthenticationFilter(@Lazy AuthenticationManager authenticationManager,
                                                         JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.setAuthenticationManager(authenticationManager);
    }

    /**
     * Attempts authentication by extracting username and password from the request.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest loginRequest;
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Error parsing login request");
        }

        String username = (loginRequest.getUsername() != null) ? loginRequest.getUsername().trim() : "";
        String password = (loginRequest.getPassword() != null) ? loginRequest.getPassword().trim() : "";

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * Generates a JWT token upon successful authentication and sets it in the response header.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        String username = authResult.getName();
        String roles = authResult.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(","));

        String jwtToken = jwtUtil.generateToken(username, roles);
        response.setHeader("Authorization", "Bearer " + jwtToken);
        response.setContentType("text/plain");
        response.getWriter().write("JWT token generated successfully! You can find it in the 'Authorization' header.");
        response.getWriter().flush();



    }
}
