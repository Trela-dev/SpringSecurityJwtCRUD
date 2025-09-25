package com.github.treladev.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the ID for each new record
    private Long id;
    private String name;

    public Role(String name) {
        this.name = name;
    }

    public Role() {
    }

}
