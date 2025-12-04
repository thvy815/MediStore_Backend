package com.example.medistore.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.user.AuthResponse;
import com.example.medistore.dto.user.ForgotPasswordRequest;
import com.example.medistore.dto.user.LoginRequest;
import com.example.medistore.dto.user.RegisterRequest;
import com.example.medistore.dto.user.ResetPasswordRequest;
import com.example.medistore.entity.user.User;
import com.example.medistore.service.user.JwtService;
import com.example.medistore.service.user.MailService;
import com.example.medistore.service.user.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final MailService mailService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {
        userService.register(req);
        return "Registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        User user = jwtService.authenticate(req.getEmail(), req.getPassword());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
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
}

