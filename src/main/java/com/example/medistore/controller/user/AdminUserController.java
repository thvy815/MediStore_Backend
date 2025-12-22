package com.example.medistore.controller.user;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.user.*;
import com.example.medistore.entity.user.User;
import com.example.medistore.service.user.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public UserResponse create(@RequestBody CreateUserRequest req) {
        return mapToResponse(userService.createUserByAdmin(req));
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAllUsers()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return mapToResponse(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest req) {
        return mapToResponse(userService.updateUser(id, req));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .roleId(user.getRole().getId())
                .roleName(user.getRole().getName())
                .build();
    }
}
