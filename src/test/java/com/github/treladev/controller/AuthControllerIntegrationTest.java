package com.github.treladev.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthControllerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;


    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    // Podmieniamy właściwości Spring Boot, żeby używał kontenera
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

    }

    @Test
    void testRegisterAndLoginRefreshIntegration() {
        // --- REGISTER ---
        String registerUrl = "http://localhost:" + port + "/api/auth/register";
        String registerBody = "{\"username\":\"newUser\",\"password\":\"validPassword123\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> registerRequest = new HttpEntity<>(registerBody, headers);

        // Send POST request to register a new user
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(registerUrl, registerRequest, String.class);

        // Verify registration was successful
        assertThat(registerResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(registerResponse.getBody()).contains("User registered successfully");

        // --- LOGIN ---
        String loginUrl = "http://localhost:" + port + "/api/auth/login";
        String loginBody = "{\"username\":\"newUser\",\"password\":\"validPassword123\"}";
        HttpEntity<String> loginRequest = new HttpEntity<>(loginBody, headers);

        // Send POST request to login
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, String.class);

        // Verify login was successful
        assertThat(loginResponse.getStatusCode().is2xxSuccessful()).isTrue();

        // Extract JWT from the Authorization header
        String jwt = loginResponse.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assertThat(jwt).isNotNull();
        assertThat(jwt).startsWith("Bearer ");

        String refreshTokenCookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(refreshTokenCookie).isNotNull();
        assertThat(refreshTokenCookie).contains("refreshToken");
        String refreshToken = refreshTokenCookie.split(";")[0].split("=")[1].trim();
        System.out.println("REFRESH TOKEN: " + refreshToken);


        String refreshUrl = "http://localhost:" + port + "/api/auth/refresh";
        String refreshBody = "{\"refreshToken\":\""  + refreshToken + "\"}";
        HttpEntity<String> refreshRequest = new HttpEntity<>(refreshBody, headers);
        ResponseEntity<String> refreshResponse = restTemplate.postForEntity(refreshUrl, refreshRequest, String.class);

        assertThat(refreshResponse.getStatusCode().is2xxSuccessful()).isTrue();




        // Verify response body contains success message and refresh token info
        assertThat(loginResponse.getBody())
                .contains("JWT token generated successfully!")
                .contains("Refresh token has been set in the 'refreshToken' cookie.");
    }


}
