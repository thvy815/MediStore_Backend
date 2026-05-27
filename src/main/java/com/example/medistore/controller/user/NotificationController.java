package com.example.medistore.controller.user;

import com.example.medistore.dto.user.NotificationResponse;
import com.example.medistore.service.user.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<NotificationResponse> getNotifications(@PathVariable UUID userId) {
        return notificationService.getNotificationsByUserId(userId);
    }

    @PutMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
    }
}
