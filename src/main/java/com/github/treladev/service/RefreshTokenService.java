package com.github.treladev.service;

import com.github.treladev.model.RefreshToken;
import com.github.treladev.model.User;
import com.github.treladev.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(User user){

        // delete token if already exists
        deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        Instant now = Instant.now().plusSeconds(7*24*60*60);
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
}
