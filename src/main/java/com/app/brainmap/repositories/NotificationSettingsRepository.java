package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.NotificationSettings;
import com.app.brainmap.domain.entities.PrivacySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    Optional<NotificationSettings> findByUserId(UUID userId);
}

