package com.github.treladev.dto;

public class UpdateUserDto {

    private String username;
    private String password;
    private String role;



    public UpdateUserDto(String role, String password, String username) {
        this.role = role;
        this.password = password;
        this.username = username;
    }



    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
