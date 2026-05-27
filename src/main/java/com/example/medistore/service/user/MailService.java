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

    // Email gửi đi sẽ là email đã cấu hình trong application.properties
    @Value("${spring.mail.username}")
    private String from;

    public void sendResetPasswordMail(String to, String token) {
        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("[MediStore] Reset your password");
        message.setText("""
                You requested a password reset.

                Click the link below to reset your password:
                """ + resetLink + """

                This link will expire in 5 minutes.
                """);

        mailSender.send(message);
    }

    public void sendVerificationMail(String to, String token) {
        try {
            String verifyLink =
                    "http://localhost:8080/api/auth/verify-email?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(from);
            message.setTo(to);
            message.setSubject("[MediStore] Verify your email");

            message.setText("""
                    Welcome to MediStore.

                    Click the link below to verify your account:

                    """ + verifyLink);

            mailSender.send(message);
            System.out.println("Verification email sent to " + to + " successfully");
        } catch (Exception e) {  
            System.out.println("Failed to send verification email to " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}

