package com.example.medistore.service.user;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.medistore.entity.user.Role;
import com.example.medistore.entity.user.User;
import com.example.medistore.repository.user.UserRepository;

import java.util.Date;
import java.util.List;

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
    public String generateAccessToken(User user) {
        List<String> roles = user.getRoles()
            .stream()
            .map(Role::getName)
            .toList();
        
        List<String> permissions = user.getRoles()
            .stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(permission -> permission.getName())
            .distinct()
            .toList();

        return Jwts.builder()
            .subject(user.getEmail())
            .claim("roles", roles)
            .claim("permissions", permissions)
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
    public boolean isValid(String token, String email) {
        String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email)
            && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration().before(new Date());
    }

    // 🔑 Dùng cho login: verify password + trả về user
    public User authenticate(String identifier, String password) {
        User user;

        // login bằng email
        if (identifier.contains("@")) {

            user = userRepo.findByEmail(identifier)
                    .orElseThrow(() ->
                        new BadCredentialsException("Email not found"));

        } else {

            // login bằng phone
            user = userRepo.findByPhone(identifier)
                    .orElseThrow(() ->
                        new BadCredentialsException("Phone not found"));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
}

