// Updated file: src/main/java/com/app/brainmap/controllers/MessageController.java
package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.MessageDto;
import com.app.brainmap.domain.dto.MessageSummaryDto;
import com.app.brainmap.domain.dto.PrivateMessageDto;
import com.app.brainmap.domain.dto.UserChatDto;
import com.app.brainmap.domain.entities.Message;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.MessageMapper;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.MessageService;
import com.app.brainmap.services.UserService;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;
    private final MessageMapper messageMapper;

    @MessageMapping("/private-message")
    public MessageDto receivePrivateMessage(@Payload MessageDto messageDto, Principal principal) {
        System.out.println("Received private message: " + messageDto);

//        UUID actualSenderId = getUserIdFromPrincipal(principal);
//        if (actualSenderId == null) {
//            throw new RuntimeException("Unauthorized: No authentication");
//        }
//        if (!actualSenderId.equals(messageDto.getSenderId())) {
//            throw new RuntimeException("Invalid sender");
//        }

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


    @GetMapping("/users/search")
    public List<User> searchUsers(@RequestParam String query) {
        return userService.searchUsers(query);
    }
}