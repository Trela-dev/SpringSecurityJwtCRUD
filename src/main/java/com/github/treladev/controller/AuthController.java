package com.github.treladev.controller;

import com.github.treladev.dto.RefreshTokenRequestDTO;
import com.github.treladev.exception.RefreshTokenExpiredException;
import com.github.treladev.exception.RefreshTokenNotFoundException;
import com.github.treladev.model.RefreshToken;
import com.github.treladev.model.User;
import com.github.treladev.dto.LoginRequestDto;
import com.github.treladev.security.jwt.JwtUtil;
import com.github.treladev.service.RefreshTokenService;
import com.github.treladev.service.UserService;
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
    public ResponseEntity<String> register(@RequestBody User user) {
            userService.registerUser(user.getUsername(), user.getPassword());
            return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto loginRequest) {
        // This method is empty because authentication is handled by a filter.
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        // checking if refresh token exists

        log.info(refreshTokenRequestDTO.refreshToken());

        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenRequestDTO.refreshToken())
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found."));
        // checking if refresh token is not expired
        if(refreshToken.isExpired()) {
            throw new RefreshTokenExpiredException("Refresh token expired. Please log in again.");
        }
        // creating new JWT access TOKEN
        String newAccessToken = jwtUtil.generateToken(refreshToken.getUser().getUsername(), refreshToken.getUser().getRole().getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                .build();

    }


    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        refreshTokenService.deleteByToken(refreshTokenRequestDTO.refreshToken());
        return ResponseEntity.noContent().build();
    }




}
