package com.github.treladev.service;

import com.github.treladev.exception.RefreshTokenExpiredException;
import com.github.treladev.exception.RefreshTokenNotFoundException;
import com.github.treladev.model.RefreshToken;
import com.github.treladev.model.User;
import com.github.treladev.repository.RefreshTokenRepository;
import com.github.treladev.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    @Value("${jwt.refresh-token-expiration}")
    private Duration refreshTokenDuration;

    public RefreshToken createRefreshToken(User user){

        // delete token if already exists
        deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        Instant now = Instant.now().plus(refreshTokenDuration);
        RefreshToken refreshToken = new RefreshToken(user, token, now);
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public void  deleteByUserId(Long userId){
        refreshTokenRepository.deleteByUserId(userId);
    }


    public void deleteByToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    public String refreshToken(String refreshToken){
       RefreshToken token = findByToken(refreshToken)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found."));

        if(token.isExpired()) {
            throw new RefreshTokenExpiredException("Refresh token expired. Please log in again.");
        }

        // return new JWT access token
        return jwtUtil.generateToken(token.getUser().getUsername(), token.getUser().getRole().getName());
    }
}
