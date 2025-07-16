package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.UserSocialLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserSocialLinkRepository extends JpaRepository<UserSocialLink, UUID> {
}
