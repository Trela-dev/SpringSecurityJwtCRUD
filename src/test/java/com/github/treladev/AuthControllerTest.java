package com.github.treladev;


import com.github.treladev.controller.AuthController;
import com.github.treladev.repository.RoleRepository;
import com.github.treladev.repository.UserRepository;
import com.github.treladev.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test") // Run tests with "test" profile
@WebMvcTest(AuthController.class) // Test only web layer for AuthController
@Import({TestSecurityConfig.class, UserService.class}) // Import required configurations
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // Main utility for testing MVC controllers

    // Mock repositories are populated with the following data:
    // UserRepository:
    //   new User(1L, "admin", "admin", new Role("ROLE_ADMIN")),
    //   new User(2L, "moderator", "moderator", new Role("ROLE_MODERATOR")),
    //   new User(3L, "user1", "user1", new Role("ROLE_USER")),
    //   new User(4L, "user2", "user2", new Role("ROLE_USER"))
    // RoleRepository:
    //   new Role("ROLE_GUEST"),
    //   new Role("ROLE_USER"),
    //   new Role("ROLE_MODERATOR"),
    //   new Role("ROLE_ADMIN")

    @Autowired
    MockUserRepository userRepository; // Custom mock implementation
    @Autowired
    MockRoleRepository roleRepository;

    @BeforeEach
    void SetUp(){
        userRepository.initTestData();
        roleRepository.initTestData();
    }



    @Test
    @DisplayName("PUT /login - should return 200 OK with JWT token in Authorization header for valid credentials")
    void login_ShouldReturnOkAndJwtTokenInHeaderOnSuccessfulAuthentication() throws Exception {
        // Test successful login scenario
        mockMvc.perform(post("/login")
                        .servletPath("/login") // Explicit path for filter requirements
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "username": "admin",
                    "password": "admin"
                }
                """))
                // Assertions:
                .andExpect(status().isOk()) // Verify 200 status
                .andExpect(header().exists("Authorization")) // Check for auth header
                .andExpect(header().string("Authorization",
                        startsWith("Bearer "))) // Verify JWT prefix
                .andExpect(content().string(
                        "JWT token generated successfully! You can find it in the 'Authorization' header."));
    }

    @Test
    @DisplayName("POST /login - should return 401 Unauthorized for invalid credentials")
    void login_ShouldReturnUnauthorizedForInvalidCredentials() throws Exception {
        // Test failed login scenario
        mockMvc.perform(post("/login")
                        .servletPath("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "username": "unknown_user",
                    "password": "wrong_password"
                }
                """))
                // Assertions:
                .andExpect(status().isUnauthorized()) // Verify 401 status
                .andExpect(header().doesNotExist("Authorization")) // No auth header
                .andExpect(content().string(containsString("Invalid credentials"))); // Error message
    }

    @Test
    @DisplayName("POST /register - should return 200 OK when registration is successful")
    void register_ShouldReturnOkWhenSuccessful() throws Exception {
        // Test successful registration

        System.out.println("LISTA WSZYSKTICH UZTYTKOWNIKOW" +  userRepository.findAll());
        mockMvc.perform(post("/register")
                        .servletPath("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "username": "newUser",
                    "password": "validPassword123"
                }
                """))
                // Assertions:
                .andExpect(status().isOk()) // Verify 200 status
                .andExpect(content().string("User registered successfully!"))
                .andExpect(result -> assertTrue(userRepository.findAll().size() == 5)); // Assert user count increment

    }

    @Test
    @DisplayName("POST /register - should return 409 Conflict when username already exists")
    void register_ShouldReturnConflictWhenUsernameExists() throws Exception {
        // Test registration conflict
        mockMvc.perform(post("/register")
                        .servletPath("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "username": "user1",
                    "password": "user1"
                }
                """))
                // Assertions:
                .andExpect(status().isConflict()) // Verify 409 status
                .andExpect(content().string(containsString("is already in use."))); // Partial error message
    }
}