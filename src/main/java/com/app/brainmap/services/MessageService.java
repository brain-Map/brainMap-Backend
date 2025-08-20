package com.app.brainmap.services;

import com.app.brainmap.domain.dto.MessageDto;
import com.app.brainmap.domain.dto.MessageSummaryDto;
import com.app.brainmap.domain.entities.Message;
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
}
