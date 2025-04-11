package com.github.treladev.controller;

import com.github.treladev.model.User;
import com.github.treladev.dto.LoginRequestDto;
import com.github.treladev.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// This is a REST controller that handles authentication-related endpoints.
@RestController
public class AuthController {

    private final UserService userService;

    // Injecting UserService via constructor
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint for user registration.
     * Checks if the username is already taken; if not, registers the user.
     *
     * @param user The user object containing username and password.
     * @return ResponseEntity with success or failure message.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
            userService.registerUser(user.getUsername(), user.getPassword());
            return ResponseEntity.ok("User registered successfully!");
    }


    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto loginRequest) {
        // This method is empty because authentication is handled by a filter.
    }
}
