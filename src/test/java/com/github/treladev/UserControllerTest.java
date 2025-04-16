package com.github.treladev;

import com.github.treladev.controller.UserController;
import com.github.treladev.dto.UpdateUserDto;
import com.github.treladev.model.Role;
import com.github.treladev.model.User;
import com.github.treladev.repository.RoleRepository;
import com.github.treladev.security.jwt.JwtUtil;
import com.github.treladev.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;


import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {


    @Autowired
    private UserController userController;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RoleRepository roleRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;




    @Test
    @DisplayName("GET /users - Should return 200 for valid token")
    void getAllUsers_ShouldReturnStatus200ForValidToken() throws Exception {
        // Test data preparation
        User user1 = new User("user1", "user1", new Role("ROLE_GUEST"));
        User user2 = new User("user2", "user2", new Role("ROLE_GUEST"));
        List<User> mockUsers = List.of(user1, user2);
        String mockValidJwtToken = "valid.jwt.token";
        when(jwtUtil.validateToken(mockValidJwtToken)).thenReturn(true);
        when(jwtUtil.extractRoles(mockValidJwtToken)).thenReturn("ROLE_ADMIN");

        // Mocking behavior
        when(userService.getAllUsers()).thenReturn(mockUsers);
        when(jwtUtil.validateToken(mockValidJwtToken)).thenReturn(true);

        // Perform GET request with valid token
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + mockValidJwtToken))
                .andExpect(status().isOk())  // Expected status 200 OK
                .andExpect(jsonPath("$").isArray())  // Check if the response is an array (list of users)
                .andExpect(jsonPath("$[0].username").value("user1"))  // Check if the first user in the list has username "user1"
                .andExpect(jsonPath("$[1].username").value("user2"));  // Check if the second user in the list has username "user2"
    }


    @Test
    @DisplayName("GET /users - Should return 401 for invalid token")
    void getAllUsers_ShouldReturnStatus401ForInvalidToken() throws Exception {
        // Test data preparation
        User user1 = new User("user1", "user1", new Role("ROLE_GUEST"));
        User user2 = new User("user2", "user2", new Role("ROLE_GUEST"));
        List<User> mockUsers = List.of(user1, user2);
        String mockInvalidJwtToken = "invalid.jwt.token";
        // Mocking behavior
        when(userService.getAllUsers()).thenReturn(mockUsers);
        when(jwtUtil.validateToken(mockInvalidJwtToken)).thenReturn(false);
        // Perform GET request with invalid token
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + mockInvalidJwtToken)
                        .with(user("admin").roles("USER")))  // Assign roles to the user
                .andExpect(status().isUnauthorized())  // Expected status 401 Unauthorized
                .andExpect(content().string("JWT Authentication failed."));  // Check if the response contains the unauthorized message
    }




    @Test
    @DisplayName("PUT /users/{id} - Should update user profile and return success message for valid token")
    void updateUserProfile_ShouldUpdateUserForValidToken() throws Exception {
        // Test data preparation
        Role mockRole = new Role("ROLE_USER");
        String mockValidJwtToken = "valid.jwt.token";

        // Mocking behavior
        when(jwtUtil.validateToken(mockValidJwtToken)).thenReturn(true);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(mockRole));
        User updatedUser = new User("updatedUsername", "updatedPassword", mockRole);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // Perform PUT request with valid token
        mockMvc.perform(put("/users/1")
                        .header("Authorization", "Bearer " + mockValidJwtToken)
                        .with(user("moderator").roles("MODERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "updatedUsername",
                            "password": "updatedPassword",
                            "role": "ROLE_USER"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(content().string("User with ID 1 has been successfully updated!"));
    }

    @Test
    @DisplayName("PUT /users/{id} - Should return 401 for invalid token")
    void updateUserProfile_ShouldReturnStatus401ForInvalidToken() throws Exception {
        // Test data preparation
        String mockInvalidJwtToken = "invalid.jwt.token";

        // Mocking behavior
        when(jwtUtil.validateToken(mockInvalidJwtToken)).thenReturn(false);

        // Perform PUT request with invalid token
        mockMvc.perform(put("/users/1")
                        .header("Authorization", "Bearer " + mockInvalidJwtToken)
                        .with(user("moderator").roles("MODERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "updatedUsername",
                            "password": "updatedPassword",
                            "role": "ROLE_USER"
                        }
                    """))
                .andExpect(status().isUnauthorized())  // Expected status 401 Unauthorized
                .andExpect(content().string("JWT Authentication failed."));  // Expected response message
    }





    @Test
    @DisplayName("When ADMIN deletes user by ID, should return success message with deleted user ID for valid token")
    void deleteUserById_ShouldDeleteUserAndReturnSuccessMessageForValidToken() throws Exception {
        // Test data
        String mockValidJwtToken = "valid.jwt.token";
        when(jwtUtil.validateToken(mockValidJwtToken)).thenReturn(true);
        when(jwtUtil.extractRoles(mockValidJwtToken)).thenReturn("ROLE_ADMIN");

        // Mocking behavior
        when(jwtUtil.validateToken(mockValidJwtToken)).thenReturn(true);
        doNothing().when(userService).deleteUserById(1L);

        // Perform DELETE request with valid token
        mockMvc.perform(delete("/users/1")
                        .header("Authorization", "Bearer " + mockValidJwtToken))
                .andExpect(status().isOk())  // Expected status 200 OK
                .andExpect(content().string("User with ID 1 has been successfully deleted."));  // Success message
    }


    @Test
    @DisplayName("When ADMIN deletes user by ID, should return 401 for invalid token")
    void deleteUserById_ShouldReturnStatus401ForInvalidToken() throws Exception {
        // Test data
        String mockInvalidJwtToken = "invalid.jwt.token";

        // Mocking behavior
        when(jwtUtil.validateToken(mockInvalidJwtToken)).thenReturn(false);

        // Perform DELETE request with invalid token
        mockMvc.perform(delete("/users/1")
                        .header("Authorization", "Bearer " + mockInvalidJwtToken))
                .andExpect(status().isUnauthorized())  // Expected status 401 Unauthorized
                .andExpect(content().string("JWT Authentication failed."));  // Expected error message
    }





}