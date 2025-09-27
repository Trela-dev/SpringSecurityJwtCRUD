package com.github.treladev.security;

import com.github.treladev.repository.UserRepository;
import com.github.treladev.security.jwt.JWTCustomUsernamePasswordAuthenticationFilter;
import com.github.treladev.security.jwt.JwtAuthenticationProvider;
import com.github.treladev.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
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
@RequiredArgsConstructor
//@Profile("!test")
public class SecurityConfig {

    private final JWTCustomUsernamePasswordAuthenticationFilter jwtCustomUsernamePasswordAuthenticationFilter;
    private final JwtFilter jwtFilter;




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

        http.logout(logout -> logout.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register/**").permitAll()
                .requestMatchers("/api/auth/login/**").permitAll()
                .requestMatchers("/api/auth/refresh/**").permitAll()
                .requestMatchers("/api/auth/logout/**").permitAll()
                //.requestMatchers("/error/**").permitAll()
                .anyRequest().authenticated());

        // Add custom authentication filters
        http.addFilterAt(jwtCustomUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtFilter, JWTCustomUsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
