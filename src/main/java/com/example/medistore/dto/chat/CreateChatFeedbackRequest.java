package com.example.medistore.dto.chat;

import java.util.UUID;

import lombok.Data;

@Data
public class CreateChatFeedbackRequest {
    private UUID sessionId;
    private Integer rating;
    private String comment;
}
