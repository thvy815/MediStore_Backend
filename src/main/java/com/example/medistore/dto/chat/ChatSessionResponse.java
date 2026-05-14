package com.example.medistore.dto.chat;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatSessionResponse {
    private UUID id;
    private UUID userId;
    private String type;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
}
