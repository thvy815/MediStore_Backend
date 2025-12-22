package com.example.medistore.dto.user;

import lombok.*;
import java.util.UUID;

@Builder @Data
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private UUID roleId;
    private String roleName;
}
