// filepath: /home/axob12/Desktop/BrainMap/brainMap-Backend/src/main/java/com/app/brainmap/services/impl/NotificationServiceImpl.java
package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.Notification;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.NotificationRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Notification createNotification(UUID recipientId, String title, String body, String type, String data) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient user not found"));

        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .recipient(recipient)
                .title(title)
                .body(body)
                .type(type)
                .data(data)
                .isRead(false)
                .build();
        Notification saved = notificationRepository.save(notification);

        // send real-time websocket notification to the recipient (if they are connected)
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", saved.getId());
            payload.put("title", saved.getTitle());
            payload.put("body", saved.getBody());
            payload.put("type", saved.getType());
            payload.put("data", saved.getData());
            payload.put("isRead", saved.getIsRead());
            payload.put("createdAt", saved.getCreatedAt());
            // Use userId-based destination: /user/{userId}/queue/notifications
            if (recipient.getId() != null) {
                String dest = "/user/" + recipient.getId().toString() + "/queue/notifications";
                messagingTemplate.convertAndSend(dest, payload);
            }
        } catch (Exception ex) {
            // don't fail the flow if websocket send fails
        }

        return saved;
    }

    @Override
    public List<Notification> getNotificationsForUser(UUID userId) {
        return notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Notification markAsRead(UUID notificationId, UUID userId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!n.getRecipient().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to mark this notification");
        }
        n.setIsRead(true);
        Notification saved = notificationRepository.save(n);

        // send websocket update about the changed notification
        try {
            if (saved.getRecipient() != null && saved.getRecipient().getId() != null) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("id", saved.getId());
                payload.put("title", saved.getTitle());
                payload.put("body", saved.getBody());
                payload.put("type", saved.getType());
                payload.put("data", saved.getData());
                payload.put("isRead", saved.getIsRead());
                payload.put("createdAt", saved.getCreatedAt());
                String dest = "/user/" + saved.getRecipient().getId().toString() + "/queue/notifications";
                messagingTemplate.convertAndSend(dest, payload);
            }
        } catch (Exception ex) {
            // ignore
        }

        return saved;
    }
}
