package com.example.medistore.dto.user;

import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class UserInfoResponse {
    private UUID id;
    private Set<String> roles;
    private String email;
    private String fullName;
    private Boolean isVerified;
    private Boolean isActive;
}