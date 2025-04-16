package com.github.treladev;


import com.github.treladev.controller.UserController;
import com.github.treladev.model.Role;
import com.github.treladev.model.User;
import com.github.treladev.repository.UserRepository;
import com.github.treladev.security.jwt.JwtSpringSecurityToken;
import com.github.treladev.security.jwt.JwtUtil;
import com.github.treladev.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@Import({TestSecurityConfig.class, UserService.class})
public class UserServiceUpdateMethodTest {

    private static final Logger log = LoggerFactory.getLogger(UserServiceUpdateMethodTest.class);
    @Autowired
    private MockUserRepository userRepository;

    @Autowired
    private MockRoleRepository roleRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;


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


    @BeforeEach
    void setUp(){
        userRepository.initTestData();
        roleRepository.initTestData();
    }

    @Test
    @DisplayName("Should return OK when a moderator updates a non-admin user")
    void updateUser_shouldReturnOk_whenModeratorUpdatesNonAdminUser()
    throws Exception {
        String mockValidJwtToken = "valid.jwt.token";
        when(jwtUtil.validateToken(mockValidJwtToken)).thenReturn(true);
        when(jwtUtil.extractRoles(mockValidJwtToken)).thenReturn("ROLE_MODERATOR");
        // Perform PUT request with valid token
        mockMvc.perform(put("/users/3")
                        .header("Authorization", "Bearer " + mockValidJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "updatedUsername",
                            "password": "updatedPassword",
                            "role": "ROLE_USER"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User with ID 3 has been successfully updated!"));
    }



    @Test
    @DisplayName("Should deny access when a moderator tries to update an admin user")
    void updateUser_shouldDenyAccess_whenModeratorUpdatesAdminUser() throws Exception{
        String mockValidJwtToken = "valid.jwt.token";
        when(jwtUtil.validateToken(mockValidJwtToken)).thenReturn(true);
        when(jwtUtil.extractRoles(mockValidJwtToken)).thenReturn("ROLE_MODERATOR");
        // Perform PUT request with valid token
        mockMvc.perform(put("/users/1")
                        .header("Authorization", "Bearer " + mockValidJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "updatedUsername",
                            "password": "updatedPassword",
                            "role": "ROLE_USER"
                        }
                    """))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string("Only admins can update other admins."));

    }

    @Test
    @DisplayName("Should return FORBIDDEN when a moderator attempts to assign the ADMIN role to another user")
    void updateUser_shouldReturnForbidden_whenModeratorAssignsAdminRole()
            throws Exception {
        String mockValidJwtToken = "valid.jwt.token";
        when(jwtUtil.validateToken(mockValidJwtToken)).thenReturn(true);
        when(jwtUtil.extractRoles(mockValidJwtToken)).thenReturn("ROLE_MODERATOR");
        // Perform PUT request with valid token
        mockMvc.perform(put("/users/3")
                        .header("Authorization", "Bearer " + mockValidJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "updatedUsername",
                            "password": "updatedPassword",
                            "role": "ROLE_ADMIN"
                        }
                    """))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string("Only admins can assign the ADMIN role."));

    }


    @Test
    @DisplayName("Should return OK when an admin updates another admin user")
    void updateUser_shouldReturnOk_whenAdminUpdatesAnotherAdmin()
            throws Exception {
        String mockValidJwtToken = "valid.jwt.token";
        when(jwtUtil.validateToken(mockValidJwtToken)).thenReturn(true);
        when(jwtUtil.extractRoles(mockValidJwtToken)).thenReturn("ROLE_ADMIN");
        // Perform PUT request with valid token
        mockMvc.perform(put("/users/1")
                        .header("Authorization", "Bearer " + mockValidJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "updatedUsername",
                            "password": "updatedPassword",
                            "role": "ROLE_USER"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User with ID 1 has been successfully updated!"));
    }

    @Test
    @DisplayName("Should return OK when an admin assigns the ADMIN role to another user")
    void updateUser_shouldReturnOk_whenAdminAssignsAdminRole()
            throws Exception {
        String mockValidJwtToken = "valid.jwt.token";
        when(jwtUtil.validateToken(mockValidJwtToken)).thenReturn(true);
        when(jwtUtil.extractRoles(mockValidJwtToken)).thenReturn("ROLE_ADMIN");
        // Perform PUT request with valid token
        mockMvc.perform(put("/users/2")
                        .header("Authorization", "Bearer " + mockValidJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "updatedUsername",
                            "password": "updatedPassword",
                            "role": "ROLE_ADMIN"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User with ID 2 has been successfully updated!"));
    }





    @Test
    @DisplayName("Should return FORBIDDEN when a regular user attempts to update another user")
    void updateUser_shouldReturnForbidden_whenUserTriesToUpdateAnotherUser()
            throws Exception {
        String mockValidJwtToken = "valid.jwt.token";
        when(jwtUtil.validateToken(mockValidJwtToken)).thenReturn(true);
        when(jwtUtil.extractRoles(mockValidJwtToken)).thenReturn("ROLE_USER");
        // Perform PUT request with valid token
        mockMvc.perform(put("/users/1")
                        .header("Authorization", "Bearer " + mockValidJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "updatedUsername",
                            "password": "updatedPassword",
                            "role": "ROLE_USER"
                        }
                    """))
                .andExpect(status().isForbidden());

    }












}
