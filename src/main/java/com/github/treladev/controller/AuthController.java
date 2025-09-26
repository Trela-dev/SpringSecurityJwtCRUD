package com.github.treladev.controller;

import com.github.treladev.dto.RefreshTokenRequestDTO;
import com.github.treladev.dto.RegisterRequestDTO;
import com.github.treladev.dto.LoginRequestDTO;

import com.github.treladev.security.jwt.JwtUtil;
import com.github.treladev.service.RefreshTokenService;
import com.github.treladev.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// This is a REST controller that handles authentication-related endpoints.
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO) {
            userService.registerUser(registerRequestDTO.username(), registerRequestDTO.password());
            return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public void login(@RequestBody @Valid LoginRequestDTO loginRequest) {
        // This method is empty because authentication is handled by a filter.
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + refreshTokenService.refreshToken(refreshTokenRequestDTO.refreshToken()))
                .build();
    }


    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        refreshTokenService.deleteByToken(refreshTokenRequestDTO.refreshToken());
        return ResponseEntity.noContent().build();
    }




}
