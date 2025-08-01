package com.app.brainmap.controllers;

import com.app.brainmap.domain.MessageType;
import com.app.brainmap.domain.dto.ChatMessageDto;
import com.app.brainmap.security.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDto sendMessage(@Payload ChatMessageDto chatMessageDto, Principal principal) {
        if (principal != null && principal instanceof Authentication) {
            Authentication auth = (Authentication) principal;
            JwtUserDetails userDetails = (JwtUserDetails) auth.getPrincipal();
            chatMessageDto.setSender(userDetails.getEmail());
        }
        chatMessageDto.setTimestamp(LocalDateTime.now());
        log.info("Public Message: ", chatMessageDto);
        return chatMessageDto;
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessageDto chatMessageDto, Principal principal) {
        if (principal != null && principal instanceof Authentication) {
            Authentication auth = (Authentication) principal;
            JwtUserDetails userDetails = (JwtUserDetails) auth.getPrincipal();
            chatMessageDto.setSender(userDetails.getEmail());
            chatMessageDto.setSenderId(userDetails.getUserId());
            chatMessageDto.setTimestamp(LocalDateTime.now());
            chatMessageDto.setPrivate(true);
            chatMessageDto.setType(MessageType.CHAT);

            // Send to receiver
            messagingTemplate.convertAndSendToUser(
                    chatMessageDto.getReceiverId().toString(),
                    "/queue/messages",
                    chatMessageDto
            );

            // Send confirmation to sender
            messagingTemplate.convertAndSendToUser(
                    userDetails.getUserId().toString(),
                    "/queue/messages",
                    chatMessageDto
            );

            log.info("Private Message sent from {} to {}: {}",
                    chatMessageDto.getSender(),
                    chatMessageDto.getReceiver(),
                    chatMessageDto.getContent());
        }
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessageDto addUser(@Payload ChatMessageDto chatMessageDto, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        String username = chatMessageDto.getSender();

        if (principal != null && principal instanceof Authentication) {
            Authentication auth = (Authentication) principal;
            JwtUserDetails userDetails = (JwtUserDetails) auth.getPrincipal();
            username = userDetails.getEmail();
        }
        headerAccessor.getSessionAttributes().put("username", username);
        chatMessageDto.setSender(username);
        chatMessageDto.setTimestamp(LocalDateTime.now());
        return chatMessageDto;
    }
}

