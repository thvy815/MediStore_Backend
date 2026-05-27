package com.example.medistore.dto.user;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Builder @Data
public class CreateUserRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private Set<UUID> roleIds;
}
