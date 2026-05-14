package com.example.medistore.dto.chat;

import java.util.UUID;

import lombok.Data;

@Data
public class CreateChatSessionRequest {
    private UUID userId;
    private String type; // ai, support, pharmacist
}
