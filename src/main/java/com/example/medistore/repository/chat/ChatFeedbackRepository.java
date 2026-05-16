package com.example.medistore.repository.chat;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.medistore.entity.chat.ChatFeedback;

public interface ChatFeedbackRepository extends JpaRepository<ChatFeedback, UUID> {
}
