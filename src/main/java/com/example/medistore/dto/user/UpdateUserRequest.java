package com.example.medistore.dto.user;

import lombok.*;
import java.util.UUID;

@Builder @Data
public class UpdateUserRequest {
    private String fullName;
    private String phone;
    private UUID roleId;
}
