// Updated file: src/main/java/com/app/brainmap/controllers/MessageController.java
package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.Chat.CreateGroupRequestDto;
import com.app.brainmap.domain.dto.Chat.GroupDto;
import com.app.brainmap.domain.dto.Chat.MessageDto;
import com.app.brainmap.domain.dto.Chat.MessageSummaryDto;
import com.app.brainmap.domain.dto.PrivateMessageDto;
import com.app.brainmap.domain.entities.Chat.Group;
import com.app.brainmap.domain.entities.Chat.Message;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.MessageMapper;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.MessageService;
import com.app.brainmap.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/messages")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;
    private final MessageMapper messageMapper;
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @MessageMapping("/private-message")
    public MessageDto receivePrivateMessage(@Payload MessageDto messageDto, Principal principal) {
        // Handle JOIN messages separately
        if ("JOIN".equals(messageDto.getStatus())) {
            System.out.println("User joined: " + messageDto.getSenderId());
            return null; // No broadcast for JOIN
        }

        // Save and forward MESSAGE types
        if ("MESSAGE".equals(messageDto.getStatus())) {
            messageService.saveFromDto(messageDto);
            System.out.println("Forwarding message to /user/" + messageDto.getReceiverId() + "/private");
            simpMessagingTemplate.convertAndSendToUser(
                    messageDto.getReceiverId().toString(), "/private", messageDto);
            System.out.println("Message forwarded to receiver: " + messageDto.getReceiverId());
        }
        return null; // Do not return to avoid unnecessary broadcast
    }

    @MessageMapping("/group-message")
    public void receiveGroupMessage(@Payload MessageDto messageDto) {
        logger.info("Received STOMP group-message payload: {}", messageDto);
        if ("GROUP_MESSAGE".equals(messageDto.getStatus())) {
            try {
                messageService.saveFromDto(messageDto);
                logger.info("Saved group message from {} to group {}", messageDto.getSenderId(), messageDto.getGroupId());
                // Broadcast the group message to a topic so all subscribers to the group receive it
                if (messageDto.getGroupId() != null) {
                    String destination = "/group/" + messageDto.getGroupId().toString() + "/messages";
                    simpMessagingTemplate.convertAndSend(destination, messageDto);
                    logger.info("Broadcasted group message to {}", destination);
                } else {
                    logger.warn("Group message missing groupId, cannot broadcast: {}", messageDto);
                }
            } catch (Exception e) {
                logger.error("Failed to save group message", e);
            }
        }
    }

    private UUID getUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null in MessageController");
            throw new RuntimeException("Unauthorized: No authentication");
        }

        if (principal instanceof Authentication authentication) {
            Object userPrincipal = authentication.getPrincipal();
            if (userPrincipal instanceof JwtUserDetails userDetails) {
                return userDetails.getUserId();
            }
        }

        System.out.println("Principal is not JwtUserDetails: " + principal.getClass());
        throw new RuntimeException("Unauthorized: Invalid principal type");
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages() {
        List<Message> messages = messageService.getAllMessages();
        List<MessageDto> messageDtos = messages.stream()
                .map(messageMapper::toDto)
                .toList();
        return ResponseEntity.ok(messageDtos);
    }

    @GetMapping("/chats/{userId}/summary")
    public ResponseEntity<List<MessageSummaryDto>> getChatSummaries(@PathVariable UUID userId) {
        List<MessageSummaryDto> summaries = messageService.getMessageSummaries(userId);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/chats/{userId1}/{userId2}")
    public List<PrivateMessageDto> getChatMessages(@PathVariable UUID userId1, @PathVariable UUID userId2, Principal principal) {
//        UUID authenticatedUserId = getUserIdFromPrincipal(principal);
//        if (!authenticatedUserId.equals(senderId)) {
//            throw new RuntimeException("Unauthorized");
//        }
        List<Message> messages = messageService.getPrivateMessages(userId1, userId2);
        return messages.stream().map(messageMapper::toPrivateMessageDto).collect(Collectors.toList());
    }


    /*    Group Management
     */

    @GetMapping("/users/search")
    public List<User> searchUsers(@RequestParam String query) {
        return userService.searchUsers(query);
    }

    // Get groups for user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupDto>> getGroupsForUser(@PathVariable UUID userId) {
        List<GroupDto> groups = messageService.getGroupsForUser(userId);
        return ResponseEntity.ok(groups);
    }

    // Add user to a group
    @PostMapping("/group/{groupId}/add-user/{userId}")
    public ResponseEntity<GroupDto> addUserToGroup(@PathVariable UUID groupId, @PathVariable UUID userId) {
        GroupDto updatedGroup = messageService.addUserToGroup(groupId, userId);
        return ResponseEntity.ok(updatedGroup);
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<List<MessageDto>> getGroupMessages(@PathVariable UUID groupId) {
        List<Message> messages = messageService.getGroupMessages(groupId);
        List<MessageDto> messageDtos = messages.stream()
                .map(messageMapper::toDto)
                .toList();
        return ResponseEntity.ok(messageDtos);
    }

    // Create a new group
    @PostMapping("/groups")
    public ResponseEntity<GroupDto> createGroup(@RequestBody CreateGroupRequestDto requestDto, Principal principal) {
        log.info("Creating group with members: " + requestDto);
        GroupDto groupDto = messageService.createGroup(requestDto.getName(), requestDto.getMembers(), requestDto.getProjectId());
        return ResponseEntity.ok(groupDto);
    }

    // Get all users in a group
    @GetMapping("/group/{groupId}/get-all-users")
    public ResponseEntity<List<UUID>> getAllUsersInGroup(@PathVariable UUID groupId) {
        List<User> users = messageService.getAllUsersInGroup(groupId);
        List<UUID> userIds = users.stream().map(User::getId).toList();
        return ResponseEntity.ok(userIds);
    }

    // Get group ID by project ID
    @GetMapping("/group/by-project/{projectId}")
    public ResponseEntity<UUID> getGroupIdByProjectId(@PathVariable UUID projectId) {
        UUID groupId = messageService.getGroupIdByProjectId(projectId);
        return ResponseEntity.ok(groupId);
    }

    // Remove a user from a group
    @DeleteMapping("/group/{groupId}/remove-user/{userId}")
    public ResponseEntity<UUID> removeUserFromGroup(@PathVariable UUID groupId, @PathVariable UUID userId) {
        UUID updatedGroupId = messageService.removeUserFromGroup(groupId, userId);
        return ResponseEntity.ok(updatedGroupId);
    }
}