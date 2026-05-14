package com.example.medistore.repository.chat;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.medistore.entity.chat.ChatSession;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    List<ChatSession> findByUserId(UUID userId);
    List<ChatSession> findByType(String type);
    List<ChatSession> findByStatus(String status);
    List<ChatSession> findByTypeAndStatus(String type, String status);
}
