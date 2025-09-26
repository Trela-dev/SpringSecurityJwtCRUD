package com.github.treladev.controller;


import com.github.treladev.dto.UpdateUserDTO;
import com.github.treladev.exception.NoSuchRoleException;
import com.github.treladev.model.User;
import com.github.treladev.repository.RoleRepository;
import com.github.treladev.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    // Endpoint for getting all users
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    // Endpoint for updating a user by ID
    // Permission evaluator handles update permissions logic (UserService)
    // Only Admin and Moderator can update users, but only admin can make moderator and moterators can't update admins
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUserProfile(@PathVariable long id, @RequestBody @Valid UpdateUserDTO updateUserDto) {
        userService.updateUser(id, updateUserDto);
        return ResponseEntity.ok("User with ID " + id + " has been successfully updated!");
    }

    // Endpoint for deleting a user by ID
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User with ID " + id + " has been successfully deleted.");
    }



}
