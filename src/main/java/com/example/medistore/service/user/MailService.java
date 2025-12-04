package com.example.medistore.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.reset-password.url}")
    private String resetPasswordBaseUrl;

    public void sendResetPasswordMail(String to, String token) {
        String resetLink = resetPasswordBaseUrl + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Reset your password");
        message.setText("""
                You requested a password reset.

                Click the link below to reset your password:
                """ + resetLink + """

                This link will expire in 30 minutes.
                """);

        mailSender.send(message);
    }
}

