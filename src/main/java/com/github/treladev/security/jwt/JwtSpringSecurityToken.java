package com.github.treladev.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Custom authentication token for JWT-based authentication in Spring Security.
 *
 * - Extends `AbstractAuthenticationToken` to integrate with Spring Security.
 * - Stores the JWT token and associated user information.
 * - Marks authentication as successful upon creation.
 */
public class JwtSpringSecurityToken extends AbstractAuthenticationToken {

    private final String username;
    private final String token;

    /**
     * Constructs a JWT authentication token.
     *
     * @param username     The authenticated user's username.
     * @param token        The JWT token.
     * @param authorities  The user's granted authorities (roles/permissions).
     */
    public JwtSpringSecurityToken(String username, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.token = token;
        setAuthenticated(true); // Mark authentication as successful
    }

    /**
     * Returns the JWT token as credentials.
     */
    @Override
    public Object getCredentials() {
        return token;
    }

    /**
     * Returns the authenticated user's username as the principal.
     */
    @Override
    public Object getPrincipal() {
        return username;
    }
}
