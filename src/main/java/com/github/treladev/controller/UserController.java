package com.github.treladev.controller;


import com.github.treladev.dto.UpdateUserDto;
import com.github.treladev.exception.NoSuchRoleException;
import com.github.treladev.model.Role;
import com.github.treladev.model.User;
import com.github.treladev.repository.RoleRepository;
import com.github.treladev.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("") // Base path
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public UserController(UserService userService,RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;

    }

    // Endpoint for getting all users
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    // Endpoint for updating a user by ID
    //@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUserProfile(@PathVariable long id, @RequestBody UpdateUserDto updateUserDto) {
            User updatedUser = new User();
            updatedUser.setUsername(updateUserDto.getUsername());
            updatedUser.setPassword(updateUserDto.getPassword());
            updatedUser.setRole(
                roleRepository.findByName(updateUserDto.getRole())
                        .orElseThrow(() -> new NoSuchRoleException("No such role: " + updateUserDto.getRole()))
        );
        userService.updateUser(id, updatedUser);
            return ResponseEntity.ok("User with ID " + id + " has been successfully updated!");
    }

    // Endpoint for deleting a user by ID
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User with ID " + id + " has been successfully deleted.");
    }



}
