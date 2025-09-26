package com.github.treladev.model;

// Import statements for JPA annotations
import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a User entity mapped to the database table.
 */
@Entity
@Table(name = "users")
@Data
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the ID for each new record
    private Long id;

    private String username; // Username for the user

    private String password;
    private boolean accountNonExpired=true;
    private boolean accountNonLocked=true;
    private boolean credentialsNonExpired=true;
    private boolean enabled=true;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;


    public User() {
    }

    public User(String username, String password, Role role) {
        this.password = password;
        this.username = username;
        this.role = role;
    }


    public User(Long id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }


}
