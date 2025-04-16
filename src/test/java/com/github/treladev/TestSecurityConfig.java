package com.github.treladev;

import com.github.treladev.repository.RoleRepository;
import com.github.treladev.repository.UserRepository;
import com.github.treladev.security.CustomUserDetailsService;
import com.github.treladev.security.SecurityConfig;
import com.github.treladev.security.UserUpdatePermissionEvaluator;
import com.github.treladev.security.jwt.*;
import org.mockito.Mock;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;


@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@Profile("test")
@Import({JwtUtil.class, JWTCustomUsernamePasswordAuthenticationFilter.class, JwtAuthenticationProvider.class,
        JwtFilter.class, JwtAuthenticationSuccessHandler.class,JwtAuthenticationFailureHandler.class, UserUpdatePermissionEvaluator.class})
public class TestSecurityConfig{

    private final UserRepository userRepository;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JWTCustomUsernamePasswordAuthenticationFilter jwtCustomUsernamePasswordAuthenticationFilter;
    private final JwtFilter jwtFilter;


    @Bean
    public UserRepository userRepository(){
        MockUserRepository mockedRepository = new MockUserRepository();
        return mockedRepository;
    }

    @Bean
    public RoleRepository roleRepository(){
        MockRoleRepository mockedRepository = new MockRoleRepository();

        return mockedRepository;
    }


    /**
     * Constructor for injecting dependencies.
     */
    public TestSecurityConfig(@Lazy UserRepository userRepository,
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
                .anyRequest().permitAll());

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
        return NoOpPasswordEncoder.getInstance();
    }







}










