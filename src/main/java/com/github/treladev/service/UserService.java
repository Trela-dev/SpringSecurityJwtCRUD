package com.github.treladev.service;


import com.github.treladev.dto.UpdateUserDTO;
import com.github.treladev.exception.DefaultRoleNotFoundException;
import com.github.treladev.exception.NoSuchRoleException;
import com.github.treladev.exception.UsernameAlreadyInUseException;
import com.github.treladev.model.Role;
import com.github.treladev.model.User;
import com.github.treladev.repository.RoleRepository;
import com.github.treladev.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        User newUser = new User(username,encryptedPassword,userRole);
        userRepository.save(newUser);

    }

    // Get all users from the repository
    public List<User> getAllUsers() {return userRepository.findAll();}

    // Update an existing user's information
    @Transactional
    @PreAuthorize("hasPermission(#id, #updateUserDTO)")
    public User updateUser(Long id, UpdateUserDTO updateUserDTO) {

        User presentUser = findUserById(id);

        presentUser.setUsername(updateUserDTO.username());
        presentUser.setPassword(passwordEncoder.encode(updateUserDTO.password()));

        Role role = roleRepository.findByName(updateUserDTO.role())
                .orElseThrow(() -> new NoSuchRoleException("No such role: " + updateUserDTO.role()));

        presentUser.setRole(role);

        return userRepository.save(presentUser);
    }


    // Delete a user by their ID
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }


    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("No user found with id " + id));


    }


    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("No user found with username " + username));
    }




}
