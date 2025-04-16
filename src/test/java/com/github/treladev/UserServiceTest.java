package com.github.treladev;


import com.github.treladev.controller.AuthController;
import com.github.treladev.repository.RoleRepository;
import com.github.treladev.repository.UserRepository;
import com.github.treladev.security.SecurityConfig;
import com.github.treladev.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.data.jpa.repositories.bootstrap-mode=none",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
})
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, UserService.class}) // Import required configurations
public class UserServiceTest {


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
    @DisplayName("GET /users - should return all users")
    void getAllUsers_shouldReturnAllUsers() {
        assertTrue(userRepository.findAll().size() == 4);
    }



   @Test
   @DisplayName("DELETE /users/{id} - should delete user by ID and reduce user count")
   void deleteUserById_shouldDeleteUser(){
       userRepository.deleteById(1L);
        assertTrue(userRepository.findAll().size()==3);
   }




}
