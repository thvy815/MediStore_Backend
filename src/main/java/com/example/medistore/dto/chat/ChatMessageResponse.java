package com.example.medistore.dto.chat;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageResponse {
    private UUID id;
    private UUID sessionId;
    private UUID senderId;
    private String senderType;
    private String message;
    private LocalDateTime createdAt;
}
