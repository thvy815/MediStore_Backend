package com.example.medistore.dto.user;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class UserInfoResponse {
    private UUID id;
    private UUID roleId;
    private String roleName;
    private String email;
    private String fullName;
}