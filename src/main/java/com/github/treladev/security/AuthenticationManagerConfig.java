package com.github.treladev.security;

import com.github.treladev.repository.UserRepository;
import com.github.treladev.security.jwt.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig {


    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UserRepository userRepository;

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(List.of(authProvider, jwtAuthenticationProvider));
    }

    /**
     * Custom UserDetailsService for loading user details from the database.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    /**
     * Configures the authentication manager with:
     * - DAO-based authentication (for username/password login).
     * - JWT-based authentication.
     */


    /**
     * Defines the password encoder for encrypting user passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
