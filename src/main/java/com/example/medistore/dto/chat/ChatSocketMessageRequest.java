package com.example.medistore.dto.chat;

import lombok.Data;
import java.util.UUID;

@Data
public class ChatSocketMessageRequest {
    private UUID sessionId;
    private UUID senderId;
    private String senderType;
    private String message;
}