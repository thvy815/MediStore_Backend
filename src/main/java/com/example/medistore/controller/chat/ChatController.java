package com.example.medistore.controller.chat;

import java.util.List;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.medistore.dto.chat.ChatMessageResponse;
import com.example.medistore.dto.chat.ChatSessionResponse;
import com.example.medistore.dto.chat.CreateChatFeedbackRequest;
import com.example.medistore.dto.chat.CreateChatSessionRequest;
import com.example.medistore.dto.chat.SendChatMessageRequest;
import com.example.medistore.service.chat.AiService;
import com.example.medistore.service.chat.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final AiService aiService;
    

    @PostMapping("/sessions")
    public ChatSessionResponse createSession(@RequestBody CreateChatSessionRequest request) {
        return chatService.createSession(request);
    }

    @GetMapping("/sessions/user/{userId}")
    public List<ChatSessionResponse> getSessionsByUser(@PathVariable UUID userId) {
        return chatService.getSessionsByUser(userId);
    }

    @GetMapping("/sessions/type/{type}")
    public List<ChatSessionResponse> getSessionsByType(@PathVariable String type) {
        return chatService.getSessionsByType(type);
    }

    @GetMapping("/sessions/type/{type}/active")
    public List<ChatSessionResponse> getActiveSessionsByType(@PathVariable String type) {
        return chatService.getActiveSessionsByType(type);
    }

    @PutMapping("/sessions/{sessionId}/close")
    public ChatSessionResponse closeSession(@PathVariable UUID sessionId) {
        return chatService.closeSession(sessionId);
    }

    @PostMapping("/messages")
public ChatMessageResponse sendMessage(@RequestBody SendChatMessageRequest request) {
    ChatMessageResponse savedMessage = chatService.sendMessage(request);

    messagingTemplate.convertAndSend(
            "/topic/chat/" + request.getSessionId(),
            savedMessage
    );

    if ("user".equalsIgnoreCase(request.getSenderType())
            && chatService.isAiSession(request.getSessionId())) {

        String aiReply = aiService.askAI(request.getMessage());

        SendChatMessageRequest aiRequest = new SendChatMessageRequest();
        aiRequest.setSessionId(request.getSessionId());
        aiRequest.setSenderId(null);
        aiRequest.setSenderType("ai");
        aiRequest.setMessage(aiReply);

        ChatMessageResponse aiMessage = chatService.sendMessage(aiRequest);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + request.getSessionId(),
                aiMessage
        );
    }

    return savedMessage;
}

    @GetMapping("/sessions/{sessionId}/messages")
    public List<ChatMessageResponse> getMessagesBySession(@PathVariable UUID sessionId) {
        return chatService.getMessagesBySession(sessionId);
    }

    @PostMapping("/feedbacks")
    public String createFeedback(@RequestBody CreateChatFeedbackRequest request) {
        chatService.createFeedback(request);
        return "Feedback submitted successfully";
    }
}
