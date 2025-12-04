package com.example.medistore.service.user;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.medistore.entity.user.User;
import com.example.medistore.repository.user.UserRepository;

import java.util.Date;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long expirationMs; // ms

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 🔐 Tạo JWT token
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // 🔍 Lấy email từ token
    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())   
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // 🔒 Kiểm tra token hợp lệ
    public boolean isValid(String token, User user) {
        String email = extractEmail(token);
        return email.equals(user.getEmail());
    }

    // 🔑 Dùng cho login: verify password + trả về user
    public User authenticate(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Email not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }

        return user;
    }
}

