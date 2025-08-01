package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.MessageType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private String content;
    private String sender;
    private UUID senderId;
    private String receiver;
    private UUID receiverId;
    private MessageType type;
    private LocalDateTime timestamp;
    private boolean isPrivate;
}
