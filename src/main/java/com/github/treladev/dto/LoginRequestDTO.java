package com.github.treladev.dto;


import jakarta.validation.constraints.NotBlank;

/**
 * DTO (Data Transfer Object) for handling login requests.
 *
 * - Used to receive username and password from the client.
 * - Contains getter and setter methods for deserialization.
 */

public record LoginRequestDTO(
        @NotBlank(message = "Username is required")
        String username,
        @NotBlank(message = "Password is required")
        String password) {

}
