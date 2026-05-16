package com.example.medistore.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.medistore.entity.user.RefreshToken;
import com.example.medistore.entity.user.User;
import com.example.medistore.repository.user.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepo;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpirationDays;

    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(refreshExpirationDays))
                .revoked(false)
                .build();

        return refreshTokenRepo.save(refreshToken);
    }

    public RefreshToken verify(String token) {

        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    public void revoke(String token) {

        RefreshToken refreshToken = verify(token);

        refreshToken.setRevoked(true);

        refreshTokenRepo.save(refreshToken);
    }
}