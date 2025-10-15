package com.app.brainmap.domain.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PrivateMessageDto {
    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private String message;
    private LocalDateTime timestamp;
}
