package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.MessageType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class MessageDto {
    private UUID senderId;
    private UUID receiverId;
    private String message;
    private String status;
}
