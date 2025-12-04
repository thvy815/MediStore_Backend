package com.example.medistore.controller.user;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.user.ChangePasswordRequest;
import com.example.medistore.entity.user.User;
import com.example.medistore.repository.user.UserRepository;
import com.example.medistore.service.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userRepo.findAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable UUID id) {
        return userRepo.findById(id).orElseThrow();
    }

    @PutMapping("/{id}")
    public User update(@PathVariable UUID id, @RequestBody User req) {
        User user = userRepo.findById(id).orElseThrow();
        user.setFullName(req.getFullName());
        user.setPhone(req.getPhone());
        user.setGender(req.getGender());
        user.setBirthDate(req.getBirthDate());
        user.setAddress(req.getAddress());
        return userRepo.save(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userRepo.deleteById(id);
    }

    @PostMapping("/{id}/change-password")
    public String changePassword(
            @PathVariable UUID id,
            @RequestBody ChangePasswordRequest req
    ) {
        userService.changePassword(id, req.getOldPassword(), req.getNewPassword());
        return "Password changed successfully";
    }
}

