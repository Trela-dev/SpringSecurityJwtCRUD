package com.github.treladev.controller;


import com.github.treladev.dto.UpdateUserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserControllerIntegrationTest {
    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }


    @Test
    void testAdminFullFlow() {
        // --- LOGIN ADMIN ---
        String loginUrl = "http://localhost:" + port + "/api/auth/login";
        String adminJson = "{\"username\":\"admin\",\"password\":\"admin\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> loginRequest = new HttpEntity<>(adminJson, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, String.class);

        String jwt = loginResponse.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assertThat(jwt).isNotNull();
        assertThat(jwt).startsWith("Bearer ");

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(jwt.replace("Bearer ", ""));

        // --- GET ALL USERS ---
        String usersUrl = "http://localhost:" + port + "/api/users";
        HttpEntity<Void> getRequest = new HttpEntity<>(authHeaders);
        ResponseEntity<String> usersResponse = restTemplate.exchange(usersUrl, HttpMethod.GET, getRequest, String.class);

        assertThat(usersResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(usersResponse.getBody()).contains("admin");


        // --- UPDATE MODERATOR ---
        String updateUrl = "http://localhost:" + port + "/api/users/2"; // user/2 is moderator
        UpdateUserDTO updateDTO = new UpdateUserDTO("updatedUser", "updatedPassword", "ROLE_MODERATOR");
        HttpEntity<UpdateUserDTO> updateRequest = new HttpEntity<>(updateDTO, authHeaders);
        ResponseEntity<String> updateResponse = restTemplate.exchange(updateUrl, HttpMethod.PUT, updateRequest, String.class);
        assertThat(updateResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(updateResponse.getBody()).contains("successfully updated");

        // --- DELETE MODERATOR ---
        String deleteUrl = "http://localhost:" + port + "/api/users/2"; // user1 ID=2
        HttpEntity<Void> deleteRequest = new HttpEntity<>(authHeaders);
        ResponseEntity<String> deleteResponse = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, deleteRequest, String.class);
        assertThat(deleteResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(deleteResponse.getBody()).contains("successfully deleted");



    }

    @Test
    void testModeratorUpdatePermissions() {
        // --- LOGIN AS MODERATOR ---
        String loginUrl = "http://localhost:" + port + "/api/auth/login";
        String moderatorJson = "{\"username\":\"moderator\",\"password\":\"moderator\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> loginRequest = new HttpEntity<>(moderatorJson, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, String.class);
        String jwt = loginResponse.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assertThat(jwt).isNotNull();
        assertThat(jwt).startsWith("Bearer ");

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(jwt.replace("Bearer ", ""));

        // --- TRY TO UPDATE ADMIN (SHOULD FAIL) ---
        String updateAdminUrl = "http://localhost:" + port + "/api/users/1"; // admin ID=1
        UpdateUserDTO updateAdminDTO = new UpdateUserDTO("newAdminName", "newAdminPass", "ROLE_ADMIN");
        HttpEntity<UpdateUserDTO> updateAdminRequest = new HttpEntity<>(updateAdminDTO, authHeaders);
        ResponseEntity<String> updateAdminResponse = restTemplate.exchange(updateAdminUrl, HttpMethod.PUT, updateAdminRequest, String.class);

        // Moderator cannot update admin → expect 403 Forbidden
        assertThat(updateAdminResponse.getStatusCode().value()).isEqualTo(403);

        // --- TRY TO ASSIGN ADMIN ROLE TO USER (SHOULD FAIL) ---
        String updateUserToAdminUrl = "http://localhost:" + port + "/api/users/4"; // user ID=4
        UpdateUserDTO updateUserToAdminDTO = new UpdateUserDTO("user4Updated", "newPassword", "ROLE_ADMIN");
        HttpEntity<UpdateUserDTO> updateUserToAdminRequest = new HttpEntity<>(updateUserToAdminDTO, authHeaders);

        // Moderator cannot assign role ADMIN → oczekiwany 403 Forbidden
        ResponseEntity<String> updateUserToAdminResponse = restTemplate.exchange(
                updateUserToAdminUrl, HttpMethod.PUT, updateUserToAdminRequest, String.class
        );
        assertThat(updateUserToAdminResponse.getStatusCode().value()).isEqualTo(403);


        // --- TRY TO UPDATE NORMAL USER (SHOULD SUCCEED) ---
        String updateUserUrl = "http://localhost:" + port + "/api/users/3"; // user ID=3
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("updatedUserName", "updatedUserPass", "ROLE_USER");
        HttpEntity<UpdateUserDTO> updateUserRequest = new HttpEntity<>(updateUserDTO, authHeaders);
        ResponseEntity<String> updateUserResponse = restTemplate.exchange(updateUserUrl, HttpMethod.PUT, updateUserRequest, String.class);

        assertThat(updateUserResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(updateUserResponse.getBody()).contains("successfully updated");
    }


}



