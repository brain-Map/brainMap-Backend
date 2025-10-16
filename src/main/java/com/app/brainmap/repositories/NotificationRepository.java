// filepath: /home/axob12/Desktop/BrainMap/brainMap-Backend/src/main/java/com/app/brainmap/repositories/NotificationRepository.java
package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipient_IdOrderByCreatedAtDesc(UUID recipientId);
}

