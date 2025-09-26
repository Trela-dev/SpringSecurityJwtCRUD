package com.github.treladev.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.treladev.dto.LoginRequestDTO;

import com.github.treladev.model.RefreshToken;
import com.github.treladev.model.User;
import com.github.treladev.service.RefreshTokenService;
import com.github.treladev.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
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
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public JWTCustomUsernamePasswordAuthenticationFilter(UserService userService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService, @Lazy AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.setAuthenticationManager(authenticationManager);
        this.setFilterProcessesUrl("/api/auth/login");
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
        LoginRequestDTO loginRequest;
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDTO.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Error parsing login request");
        }

        String username = (loginRequest.username() != null) ? loginRequest.username().trim() : "";
        String password = (loginRequest.password() != null) ? loginRequest.password().trim() : "";

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * Generates a JWT token upon successful authentication and sets it in the response header.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {

        String username = authResult.getName();
        String roles = authResult.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(","));

        String accessToken = jwtUtil.generateToken(username, roles);

        User user = userService.findUserByUsername(username);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken.getToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);




        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setContentType("text/plain");
        response.getWriter().write(
                "JWT token generated successfully! You can find it in the 'Authorization' header.  Refresh token has been set in the 'refreshToken' cookie."
        );
        response.getWriter().flush();



    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException{

        super.unsuccessfulAuthentication(request,response,failed);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("Invalid credentials");

    }



}
