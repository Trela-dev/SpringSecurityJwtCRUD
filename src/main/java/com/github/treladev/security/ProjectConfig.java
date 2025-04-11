package com.github.treladev.security;

import com.github.treladev.repository.UserRepository;
import com.github.treladev.security.jwt.JWTCustomUsernamePasswordAuthenticationFilter;
import com.github.treladev.security.jwt.JwtAuthenticationProvider;
import com.github.treladev.security.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Spring Security configuration class.
 *
 * - Configures authentication and authorization rules.
 * - Registers custom security filters.
 * - Provides user authentication mechanisms.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ProjectConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JWTCustomUsernamePasswordAuthenticationFilter jwtCustomUsernamePasswordAuthenticationFilter;
    private final JwtFilter jwtFilter;

    /**
     * Constructor for injecting dependencies.
     */
    public ProjectConfig(UserRepository userRepository,
                         JWTCustomUsernamePasswordAuthenticationFilter jwtCustomUsernamePasswordAuthenticationFilter,
                         JwtAuthenticationProvider jwtAuthenticationProvider,
                         @Lazy JwtFilter jwtFilter) {
        this.userRepository = userRepository;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.jwtCustomUsernamePasswordAuthenticationFilter = jwtCustomUsernamePasswordAuthenticationFilter;
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configures HTTP security settings, including authentication and authorization rules.
     *
     * - Disables CSRF protection.
     * - Allows public access to `/register` and `/login`.
     * - Requires authentication for all other endpoints.
     * - Adds JWT-based authentication filters.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/register/**").permitAll()
                .requestMatchers("/login/**").permitAll()
                .anyRequest().authenticated());

        // Add custom authentication filters
        http.addFilterAt(jwtCustomUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtFilter, JWTCustomUsernamePasswordAuthenticationFilter.class);

        return http.build();
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
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(List.of(authProvider, jwtAuthenticationProvider));
    }

    /**
     * Defines the password encoder for encrypting user passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
