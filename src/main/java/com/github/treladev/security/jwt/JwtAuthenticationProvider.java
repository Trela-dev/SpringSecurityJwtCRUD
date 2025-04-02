package com.github.treladev.security.jwt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom AuthenticationProvider for JWT-based authentication.
 *
 * - Extracts and validates the JWT token.
 * - Retrieves the username and roles from the token.
 * - Converts roles into Spring Security authorities.
 * - Returns an authenticated JwtSpringSecurityToken if valid.
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            String roles = jwtUtil.extractRoles(token);
            Collection<? extends GrantedAuthority> authorities = convertStringRolesToAuthorities(roles);
            return new JwtSpringSecurityToken(username, token, authorities);
        }
        throw new AuthenticationException("Invalid JWT Token") {};
    }

    /**
     * Converts a comma-separated roles string into a collection of GrantedAuthority objects.
     */
    private Collection<? extends GrantedAuthority> convertStringRolesToAuthorities(String rolesString) {
        if (rolesString == null || rolesString.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(rolesString.split(","))
                .map(role -> new SimpleGrantedAuthority(role.trim()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtSpringSecurityToken.class.isAssignableFrom(authentication);
    }
}
