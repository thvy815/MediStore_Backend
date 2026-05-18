package com.example.medistore.dto.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatStatisticsResponse {
    private long totalSessions;
    private long activeSessions;
    private long endedSessions;
    private double averageRating;
}