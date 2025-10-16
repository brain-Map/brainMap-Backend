package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Chat.Group;
import com.app.brainmap.domain.entities.Chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("SELECT m FROM Message m WHERE (m.senderId.id = :user1 and m.receiverId.id = :user2) OR (m.senderId.id = :user2 AND m.receiverId.id = :user1) ORDER BY m.timestamp ASC")
    List<Message> findPrivateMessagesBetween(@Param("user1") UUID user1, @Param("user2") UUID user2);

    @Query("SELECT DISTINCT m.senderId.id FROM Message m WHERE m.receiverId.id = :userId " +
    "UNION SELECT DISTINCT m.receiverId.id FROM Message m WHERE m.senderId.id = :userId")
    List<UUID> findChatUserIds(UUID userId);

    @Query("SELECT m FROM Message m WHERE m.senderId.id = :userId OR m.receiverId.id = :userId ORDER BY m.timestamp ASC")
    List<Message> findAllMessagesForUser(@Param("userId") UUID userId);

    @Query("SELECT m FROM Message m WHERE ((m.senderId.id = :user1 AND m.receiverId.id = :user2) OR (m.senderId.id = :user2 AND m.receiverId.id = :user1)) ORDER BY m.timestamp DESC LIMIT 1")
    Message findTopByParticipantsOrderByTimestampDesc(@Param("user1") UUID user1, @Param("user2") UUID user2);

    @Query("SELECT m FROM Message m WHERE m.group.id = :groupId ORDER BY m.timestamp ASC")
    List<Message> findMessagesByGroupId(@Param("groupId") UUID groupId);

}
