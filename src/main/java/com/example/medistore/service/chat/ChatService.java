package com.example.medistore.service.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.medistore.dto.chat.ChatMessageResponse;
import com.example.medistore.dto.chat.ChatSessionResponse;
import com.example.medistore.dto.chat.ChatSocketMessageRequest;
import com.example.medistore.dto.chat.ChatStatisticsResponse;
import com.example.medistore.dto.chat.CreateChatFeedbackRequest;
import com.example.medistore.dto.chat.CreateChatSessionRequest;
import com.example.medistore.dto.chat.SendChatMessageRequest;
import com.example.medistore.entity.chat.ChatFeedback;
import com.example.medistore.entity.chat.ChatMessage;
import com.example.medistore.entity.chat.ChatSession;
import com.example.medistore.entity.user.User;
import com.example.medistore.repository.chat.ChatFeedbackRepository;
import com.example.medistore.repository.chat.ChatMessageRepository;
import com.example.medistore.repository.chat.ChatSessionRepository;
import com.example.medistore.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatFeedbackRepository chatFeedbackRepository;
    private final UserRepository userRepository;

    public ChatSessionResponse createSession(CreateChatSessionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatSession session = ChatSession.builder()
                .user(user)
                .type(request.getType())
                .status("active")
                .createdAt(LocalDateTime.now())
                .build();

        ChatSession saved = chatSessionRepository.save(session);

        return mapSessionToResponse(saved);
    }

    public ChatMessageResponse sendMessage(SendChatMessageRequest request) {
        ChatSession session = chatSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        if ("closed".equalsIgnoreCase(session.getStatus())) {
            throw new RuntimeException("Chat session is closed");
        }

        ChatMessage message = ChatMessage.builder()
                .session(session)
                .senderId(request.getSenderId())
                .senderType(request.getSenderType())
                .message(request.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        ChatMessage saved = chatMessageRepository.save(message);

        return mapMessageToResponse(saved);
    }

    public List<ChatMessageResponse> getMessagesBySession(UUID sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(this::mapMessageToResponse)
                .toList();
    }

    public List<ChatSessionResponse> getSessionsByUser(UUID userId) {
        return chatSessionRepository.findByUserId(userId)
                .stream()
                .map(this::mapSessionToResponse)
                .toList();
    }

    public List<ChatSessionResponse> getSessionsByType(String type) {
        return chatSessionRepository.findByType(type)
                .stream()
                .map(this::mapSessionToResponse)
                .toList();
    }

    public List<ChatSessionResponse> getActiveSessionsByType(String type) {
        return chatSessionRepository.findByTypeAndStatus(type, "active")
                .stream()
                .map(this::mapSessionToResponse)
                .toList();
    }

    public ChatSessionResponse closeSession(UUID sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        session.setStatus("closed");
        session.setEndedAt(LocalDateTime.now());

        ChatSession saved = chatSessionRepository.save(session);

        return mapSessionToResponse(saved);
    }

    public void createFeedback(CreateChatFeedbackRequest request) {
        ChatSession session = chatSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        ChatFeedback feedback = ChatFeedback.builder()
                .session(session)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        chatFeedbackRepository.save(feedback);
    }

    private ChatSessionResponse mapToChatSessionResponse(ChatSession session) {
    return ChatSessionResponse.builder()
            .id(session.getId())
            .userId(session.getUser() != null ? session.getUser().getId() : null)
            .type(session.getType())
            .status(session.getStatus())
            .createdAt(session.getCreatedAt())
            .endedAt(session.getEndedAt())
            .build();
}

    public List<ChatSessionResponse> getAllSessions() {
        return chatSessionRepository.findAll()
                .stream()
                .map(this::mapToChatSessionResponse)
                .toList();
        }

        public ChatStatisticsResponse getStatistics() {
        long total = chatSessionRepository.count();
        long active = chatSessionRepository.countByStatus("active");
        long ended = chatSessionRepository.countByStatus("ended");

        return ChatStatisticsResponse.builder()
                .totalSessions(total)
                .activeSessions(active)
                .endedSessions(ended)
                .averageRating(0.0)
                .build();
        }

    private ChatSessionResponse mapSessionToResponse(ChatSession session) {
        return ChatSessionResponse.builder()
                .id(session.getId())
                .userId(session.getUser() != null ? session.getUser().getId() : null)
                .type(session.getType())
                .status(session.getStatus())
                .createdAt(session.getCreatedAt())
                .endedAt(session.getEndedAt())
                .build();
    }

    private ChatMessageResponse mapMessageToResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .sessionId(message.getSession().getId())
                .senderId(message.getSenderId())
                .senderType(message.getSenderType())
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public ChatMessageResponse sendSocketMessage(ChatSocketMessageRequest request) {
        SendChatMessageRequest normalRequest = new SendChatMessageRequest();

        normalRequest.setSessionId(request.getSessionId());
        normalRequest.setSenderId(request.getSenderId());
        normalRequest.setSenderType(request.getSenderType());
        normalRequest.setMessage(request.getMessage());

        return sendMessage(normalRequest);
        }
        public boolean isAiSession(UUID sessionId) {
                ChatSession session = chatSessionRepository.findById(sessionId)
                        .orElseThrow(() -> new RuntimeException("Chat session not found"));

                return "ai".equalsIgnoreCase(session.getType());
         }
     
}
