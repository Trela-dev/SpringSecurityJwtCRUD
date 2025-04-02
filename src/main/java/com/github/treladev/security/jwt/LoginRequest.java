package com.github.treladev.security.jwt;


/**
 * DTO (Data Transfer Object) for handling login requests.
 *
 * - Used to receive username and password from the client.
 * - Contains getter and setter methods for deserialization.
 */

public class LoginRequest {

    private String username;
    private String password;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
