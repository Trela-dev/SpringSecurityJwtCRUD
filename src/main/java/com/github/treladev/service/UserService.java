package com.github.treladev.service;


import com.github.treladev.exception.DefaultRoleNotFoundException;
import com.github.treladev.exception.UsernameAlreadyInUseException;
import com.github.treladev.model.Role;
import com.github.treladev.model.User;
import com.github.treladev.repository.RoleRepository;
import com.github.treladev.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @Transactional
    public void registerUser(String username, String password) {
        // Encrypt the password
        //check if user already exists
        boolean isUsernameAlreadyInUse = userRepository.findByUsername(username).isPresent();
        if(isUsernameAlreadyInUse){
            throw new UsernameAlreadyInUseException("Username '" + username + "' is already in use.");
        }
        String encryptedPassword = passwordEncoder.encode(password);
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(()-> new DefaultRoleNotFoundException("Default role not found."));
        // Create new user and save to repository
        User newUser = new User(username,password,userRole);
        userRepository.save(newUser);

    }

    // Get all users from the repository
    public List<User> getAllUsers() {return userRepository.findAll();}

    // Update an existing user's information
    @Transactional
    @PreAuthorize("hasPermission(#id, #updatedUser)")
    public User updateUser(Long id, User updatedUser) {
        User presentUser = findUserById(id);
        String encryptedPassword = passwordEncoder.encode(updatedUser.getPassword());
        presentUser.setUsername(updatedUser.getUsername());
        presentUser.setPassword(encryptedPassword);
        presentUser.setRole(updatedUser.getRole());
        return userRepository.save(presentUser);
    }


    // Delete a user by their ID
    @Transactional
    public void deleteUserById(Long id) {
        User user = findUserById(id);
        userRepository.deleteById(id);
    }


    public User findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("No user found with id " + id));
        return user;
    }




}
