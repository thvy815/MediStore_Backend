package com.example.medistore.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    private String identifier; // email hoặc phone

    @NotBlank
    private String password;
}
