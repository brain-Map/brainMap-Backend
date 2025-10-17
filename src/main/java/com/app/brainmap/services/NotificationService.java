// filepath: /home/axob12/Desktop/BrainMap/brainMap-Backend/src/main/java/com/app/brainmap/services/NotificationService.java
package com.app.brainmap.services;

import com.app.brainmap.domain.entities.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    Notification createNotification(UUID recipientId, String title, String body, String type, String data);
    List<Notification> getNotificationsForUser(UUID userId);
    Notification markAsRead(UUID notificationId, UUID userId);
}

