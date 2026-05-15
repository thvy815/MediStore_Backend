package com.example.medistore.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.user.AuthResponse;
import com.example.medistore.dto.user.ForgotPasswordRequest;
import com.example.medistore.dto.user.LoginRequest;
import com.example.medistore.dto.user.RegisterRequest;
import com.example.medistore.dto.user.ResetPasswordRequest;
import com.example.medistore.dto.user.UserInfoResponse;
import com.example.medistore.entity.user.RefreshToken;
import com.example.medistore.entity.user.User;
import com.example.medistore.service.user.JwtService;
import com.example.medistore.service.user.MailService;
import com.example.medistore.service.user.RefreshTokenService;
import com.example.medistore.service.user.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final MailService mailService;
    private final RefreshTokenService refreshTokenService;
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {
        userService.register(req);
        return "Please check your email to verify your account";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        User user = jwtService.authenticate(req.getEmail(), req.getPassword());

        // check verify email
        if (!user.getIsVerified()) {
            throw new RuntimeException("Email is not verified");
        }

        // check account active
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is disabled");
        }

        // check account locked
        if (user.getIsAccountLocked()) {
            throw new RuntimeException("Account is locked");
        }

        // generate access token
        String accessToken = jwtService.generateAccessToken(user);

        // generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        UserInfoResponse userInfo = UserInfoResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .roles(
                        user.getRoles()
                        .stream()
                        .map(role -> role.getName())
                        .collect(java.util.stream.Collectors.toSet())
            )
            .isVerified(user.getIsVerified())
            .isActive(user.getIsActive())
            .build();

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getToken())
            .user(userInfo)
            .build();
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return "Email verified successfully";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordRequest req) {
        String token = userService.generateResetPasswordToken(req.getEmail());
        mailService.sendResetPasswordMail(req.getEmail(), token);
        return "Email sent";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest req) {
        userService.resetPassword(req.getToken(), req.getNewPassword());
        return "Password updated";
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam String refreshToken) {

        RefreshToken token = refreshTokenService.verify(refreshToken);

        User user = token.getUser();

        String accessToken = jwtService.generateAccessToken(user);

        UserInfoResponse userInfo = UserInfoResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .roles(
                    user.getRoles()
                            .stream()
                            .map(role -> role.getName())
                            .collect(java.util.stream.Collectors.toSet())
            )
            .isVerified(user.getIsVerified())
            .isActive(user.getIsActive())
            .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userInfo)
                .build();
    }
}

