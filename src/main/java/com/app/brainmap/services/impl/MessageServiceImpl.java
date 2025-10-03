package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.MessageDto;
import com.app.brainmap.domain.dto.MessageSummaryDto;
import com.app.brainmap.domain.entities.Message;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.MessageRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.MessageService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public void saveFromDto(MessageDto messageDto) {
        if(!"MESSAGE".equals(messageDto.getStatus())){
            return;
        }

        Message message = new Message();
        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(messageDto.getReceiverId())
                        .orElseThrow(() -> new RuntimeException("Receiver not found"));
        message.setSenderId(sender);
        message.setReceiverId(receiver);
        message.setContent(messageDto.getMessage());
        message.setStatus(messageDto.getStatus());

        messageRepository.save(message);
    }

    @Override
    public List<Message> getPrivateMessages(UUID userId1, UUID userId2) {
        return messageRepository.findPrivateMessagesBetween(userId1, userId2);
    }

    @Override
    public List<User> getChatUsers(UUID userId) {
        List<UUID> userIds = messageRepository.findChatUserIds(userId);
        return userRepository.findAllById(userIds);
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> getAllMessagesForUser(UUID userId) {
        System.out.println("Fetching all messages for user: " + userId);
        return messageRepository.findAllMessagesForUser(userId);
    }

    @Override
    public List<MessageSummaryDto> getMessageSummaries(UUID currentUserId) {
        List<User> chatUsers = getChatUsers(currentUserId);
        List<MessageSummaryDto> summaries = new ArrayList<>();
        for (User user : chatUsers) {
            Message lastMsg = messageRepository.findTopByParticipantsOrderByTimestampDesc(currentUserId, user.getId());
            summaries.add(new MessageSummaryDto(
                    user.getId(),
                    user.getFirstName() + " " + user.getLastName(),
                    lastMsg != null ? lastMsg.getContent() : null,
                    lastMsg != null ? lastMsg.getTimestamp() : null
            ));
        }
        return summaries;
    }
}
