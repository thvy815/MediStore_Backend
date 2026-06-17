package com.example.medistore.service.user;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.user.CreateUserRequest;
import com.example.medistore.dto.user.RegisterRequest;
import com.example.medistore.dto.user.UpdateUserRequest;
import com.example.medistore.entity.cart.Cart;
import com.example.medistore.entity.user.Role;
import com.example.medistore.entity.user.User;
import com.example.medistore.repository.cart.CartRepository;
import com.example.medistore.repository.user.RoleRepository;
import com.example.medistore.repository.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${app.reset-token.expiration}")
    private long resetTokenExpirySeconds;

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final MailService mailService;

    public static final String ROLE_CUSTOMER = "CUSTOMER";

    @Transactional
    public User register(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepo.findByPhone(req.getPhone()).isPresent()) {
            throw new RuntimeException("Phone already exists");
        }

        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new RuntimeException("Password cannot be empty");
        }

        // Kiểm tra ngày sinh
        if (req.getBirthDate() == null) {
            throw new RuntimeException("Birth date is required");
        }

        int age = Period.between(
                req.getBirthDate(),
                LocalDate.now()
        ).getYears();

        if (age < 18) {
            throw new RuntimeException("User must be at least 18 years old");
        }

        Role customerRole = roleRepo.findByName(ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Customer role not found"));

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .birthDate(req.getBirthDate())
                .verificationToken(UUID.randomUUID().toString())
                .verificationTokenExpiry(LocalDateTime.now().plusHours(24))
                .build();

        user.getRoles().add(customerRole);

        User savedUser = userRepo.save(user);

        // Tạo cart rỗng cho user mới
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        // send verify email
        mailService.sendVerificationMail(
                savedUser.getEmail(),
                savedUser.getVerificationToken()
        );

        return savedUser;
    }

    public void verifyEmail(String token) {
        User user = userRepo.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token expired");
        }

        user.setIsVerified(true);

        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);

        userRepo.save(user);
    }

    public String generateResetPasswordToken(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusSeconds(resetTokenExpirySeconds));

        userRepo.save(user);
        return token;
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepo.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepo.save(user);
    }

    public void changePassword(UUID userId, String oldPass, String newPass) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPass, user.getPassword())) {
            throw new RuntimeException("Old password incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPass));
        userRepo.save(user);
    }

    @Transactional
    public User createUserByAdmin(CreateUserRequest req) {

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
            .email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .fullName(req.getFullName())
            .phone(req.getPhone())
            .build();

        if (req.getRoleIds() != null && !req.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepo.findAllById(req.getRoleIds());

            if (roles.size() != req.getRoleIds().size()) {
                throw new RuntimeException("One or more roles not found");
            }

            user.getRoles().addAll(roles);
        }

        User savedUser = userRepo.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        return savedUser;
    }

    public User updateUser(UUID userId, UpdateUserRequest req) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());

        if (req.getRoleIds() != null) {

            List<Role> roles = roleRepo.findAllById(req.getRoleIds());

            if (roles.size() != req.getRoleIds().size()) {
                throw new RuntimeException("One or more roles not found");
            }

            user.getRoles().clear();
            user.getRoles().addAll(roles);
        }

        return userRepo.save(user);
    }

    public void deleteUser(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsDeleted(true);
    }

    public User getUserById(UUID userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
}
