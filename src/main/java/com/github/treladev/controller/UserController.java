package com.github.treladev.controller;


import com.github.treladev.model.User;
import com.github.treladev.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("") // Base path
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;

    }

    // Endpoint for getting all users
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    // Endpoint for updating a user by ID
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUserProfile(@PathVariable long id, @RequestBody User updatedUser) {
        boolean userExists = userService.doesUserExist(id);
        if (userExists) {
            userService.updateUser(id, updatedUser);
            return ResponseEntity.ok("User with ID " + id + " has been successfully updated!");
        } else {
            return ResponseEntity.status(404).body("No user found with ID " + id + ".");
        }
    }

    // Endpoint for deleting a user by ID
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        boolean isDeleted = userService.deleteUserById(id);
        if (isDeleted) {
            return ResponseEntity.ok("User with ID " + id + " has been successfully deleted.");
        } else {
            return ResponseEntity.status(404).body("No user found with ID " + id + ".");
        }
    }



}
