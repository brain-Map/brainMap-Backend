package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.Chat.GroupDto;
import com.app.brainmap.domain.dto.Chat.MessageDto;
import com.app.brainmap.domain.dto.Chat.MessageSummaryDto;
import com.app.brainmap.domain.entities.Chat.Group;
import com.app.brainmap.domain.entities.Chat.Message;
import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.GroupRepository;
import com.app.brainmap.repositories.MessageRepository;
import com.app.brainmap.repositories.ProjectRepositiory;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.MessageService;
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
    private final GroupRepository groupRepository;
    private final ProjectRepositiory projectRepository;

    @Override
    public void saveFromDto(MessageDto messageDto) {
        if ("MESSAGE".equals(messageDto.getStatus())) {
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
        } else if ("GROUP_MESSAGE".equals(messageDto.getStatus())) {
            Message message = new Message();
            User sender = userRepository.findById(messageDto.getSenderId())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            message.setSenderId(sender);
            message.setContent(messageDto.getMessage());
            message.setStatus(messageDto.getStatus());
            if (messageDto.getGroupId() != null) {
                Group group = groupRepository.findById(messageDto.getGroupId())
                        .orElseThrow(() -> new RuntimeException("Group not found"));
                message.setGroup(group);
            }
            messageRepository.save(message);
        }
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

    @Override
    public List<GroupDto> getGroupsForUser(UUID userId) {
        List<Group> groups = groupRepository.findGroupsByMemberId(userId);
        return groups.stream().map(this::toGroupDto).toList();
    }

    @Override
    public GroupDto addUserToGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        group.getMembers().add(user);
        Group saved = groupRepository.save(group);
        return toGroupDto(saved);
    }

    @Override
    public List<Message> getGroupMessages(UUID groupId) {
        return messageRepository.findMessagesByGroupId(groupId);
    }

    @Override
    public GroupDto createGroup(String name, List<UUID> memberIds, UUID projectId) {
        Group group = new Group();
        group.setName(name);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        group.setProject(project);
        List<User> members = userRepository.findAllById(memberIds);
        group.setMembers(new java.util.HashSet<>(members));
        Group saved = groupRepository.save(group);
        return toGroupDto(saved);
    }

    private GroupDto toGroupDto(Group group) {
        List<UUID> memberIds = group.getMembers().stream()
            .map(User::getId)
            .toList();
        return new GroupDto(group.getId(), group.getName(), memberIds);
    }

    @Override
    public List<User> getAllUsersInGroup(UUID groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return new java.util.ArrayList<>(group.getMembers());
    }

    @Override
    public UUID getGroupIdByProjectId(UUID projectId) {
        Group group = groupRepository.findByProjectId(projectId);
        if (group == null) {
            throw new RuntimeException("Group not found for project id: " + projectId);
        }
        return group.getId();
    }

    @Override
    public UUID removeUserFromGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean removed = group.getMembers().remove(user);
        if (!removed) {
            throw new RuntimeException("User not in group");
        }
        groupRepository.save(group);
        return group.getId();
    }
}
