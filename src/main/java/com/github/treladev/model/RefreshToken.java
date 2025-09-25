package com.github.treladev.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Data
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable =false)
    private User user;


    @Column(nullable = false)
    private Instant expiryDate;

    public boolean isExpired(){
        return Instant.now().isAfter(expiryDate);
    }

    public RefreshToken(User user, String token, Instant expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
