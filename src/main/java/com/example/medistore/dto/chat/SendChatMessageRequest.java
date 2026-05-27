package com.example.medistore.dto.chat;

import java.util.UUID;

import lombok.Data;

@Data
public class SendChatMessageRequest {
    private UUID sessionId;
    private UUID senderId;
    private String senderType; // user, staff, ai
    private String message;
}
