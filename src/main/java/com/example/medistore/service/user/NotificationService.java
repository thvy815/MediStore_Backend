package com.example.medistore.service.user;

import com.example.medistore.dto.user.NotificationResponse;
import com.example.medistore.entity.user.Notification;
import com.example.medistore.enums.NotificationType;
import com.example.medistore.repository.user.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(
            UUID userId,
            String title,
            String message,
            NotificationType type
    ) {

        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        NotificationResponse response =
                NotificationResponse.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .type(notification.getType())
                        .isRead(notification.getIsRead())
                        .createdAt(notification.getCreatedAt())
                        .build();

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                response
        );
    }

    public List<NotificationResponse> getNotificationsByUserId(
            UUID userId
    ) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notification ->
                        NotificationResponse.builder()
                                .id(notification.getId())
                                .title(notification.getTitle())
                                .message(notification.getMessage())
                                .type(notification.getType())
                                .isRead(notification.getIsRead())
                                .createdAt(notification.getCreatedAt())
                                .build()
                )
                .toList();
    }

    public void markAsRead(UUID notificationId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        notification.setIsRead(true);

        notificationRepository.save(notification);
    }
}