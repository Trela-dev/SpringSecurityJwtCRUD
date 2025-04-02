package com.github.treladev.service;


import com.github.treladev.model.Role;
import com.github.treladev.model.User;
import com.github.treladev.repository.RoleRepository;
import com.github.treladev.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor to initialize UserRepository and PasswordEncoder
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    // Register a new user with encrypted password
    public void registerUser(String username, String password) {
        // Encrypt the password
        String encryptedPassword = passwordEncoder.encode(password);


     Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(()-> new RuntimeException("Default role not found."));

        // Create new user and save to repository
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encryptedPassword);
        newUser.setRole(userRole);
        //newUser.setAccountNonExpired(true);
        
        userRepository.save(newUser);
    }

    // Get all users from the repository
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update an existing user's information
    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        User userToUpdate = userRepository.findById(id).orElseThrow();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isCurrentUserAdmin =  authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isUserToUpdateAdmin = userToUpdate.getRole().getName().equals("ROLE_ADMIN");

        if(isUserToUpdateAdmin && !isCurrentUserAdmin){
            throw new RuntimeException("You cannot update ADMIN.");
        }

        String encryptedPassword = passwordEncoder.encode(updatedUser.getPassword());
        user.setUsername(updatedUser.getUsername());
        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    // Delete a user by their ID
    public boolean deleteUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Check if a user exists by their ID
    public boolean doesUserExist(Long id) {
        return userRepository.existsById(id);
    }

    // Get a user by their username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
