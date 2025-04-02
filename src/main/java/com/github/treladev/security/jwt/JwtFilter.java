package com.github.treladev.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Filter for intercepting HTTP requests and validating JWT tokens.
 *
 * - Filters requests to ensure only authenticated users can access protected resources.
 * - Extracts and validates the JWT token from the Authorization header.
 * - If valid, sets the authentication in the SecurityContext.
 * - If invalid or missing, responds with a 403 Forbidden status.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    private final JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;

    public JwtFilter(JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                     JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler,
                     JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
        this.jwtAuthenticationFailureHandler = jwtAuthenticationFailureHandler;
    }

    /**
     * Intercepts each request, checks for JWT authentication, and either allows or blocks access.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String path = request.getServletPath();

        // Allow public endpoints (login and register) without JWT authentication
        if ("/login".equals(path) || "/register".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Validate JWT token if present
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            JwtSpringSecurityToken jwtAuthenticationToken = new JwtSpringSecurityToken(null, token, null);
            try {
                Authentication authenticationResult = authenticationManager.authenticate(jwtAuthenticationToken);
                successfulJwtAuthentication(request, response, filterChain, authenticationResult);
            } catch (AuthenticationException exception) {
                unsuccessfulJwtAuthentication(request, response, filterChain, exception);
            }
        } else {
            // Reject requests without a valid JWT token
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain");
            response.getWriter().write("Unauthorized - JWT token required");
            return;
        }
    }

    /**
     * Handles successful authentication by setting the SecurityContext and proceeding with the request.
     */
    private void successfulJwtAuthentication(HttpServletRequest request, HttpServletResponse response,
                                             FilterChain filterChain, Authentication successAuthenticationToken)
            throws IOException, ServletException {

        SecurityContextHolder.getContext().setAuthentication(successAuthenticationToken);
        jwtAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, successAuthenticationToken);
        filterChain.doFilter(request, response);
        logger.info("Current context: {}", SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Handles authentication failures by delegating to the failure handler.
     */
    private void unsuccessfulJwtAuthentication(HttpServletRequest request, HttpServletResponse response,
                                               FilterChain filterChain, AuthenticationException exception)
            throws IOException, ServletException {
        jwtAuthenticationFailureHandler.onAuthenticationFailure(request, response, exception);
    }
}
