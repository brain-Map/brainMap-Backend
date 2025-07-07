package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.CommunityReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommunityReplyRepository extends JpaRepository<CommunityReply, UUID> {
}
