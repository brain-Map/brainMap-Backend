package com.app.brainmap.services;

import com.app.brainmap.domain.dto.Chat.GroupDto;
import com.app.brainmap.domain.dto.Chat.MessageDto;
import com.app.brainmap.domain.dto.Chat.MessageSummaryDto;
import com.app.brainmap.domain.entities.Chat.Message;
import com.app.brainmap.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void saveFromDto(MessageDto messageDto);
    List<Message> getPrivateMessages(UUID userId1, UUID userId2);
    List<User> getChatUsers(UUID userId);
    List<Message> getAllMessages();
    List<Message> getAllMessagesForUser(UUID userId);
    List<MessageSummaryDto> getMessageSummaries(UUID userId);
    List<GroupDto> getGroupsForUser(UUID userId);
    GroupDto addUserToGroup(UUID groupId, UUID userId);
    UUID removeUserFromGroup(UUID groupId, UUID userId);
    List<Message> getGroupMessages(UUID groupId);
    GroupDto createGroup(String name, List<UUID> memberIds, UUID projectId);
    List<User> getAllUsersInGroup(UUID groupId);
    UUID getGroupIdByProjectId(UUID projectId);
}

