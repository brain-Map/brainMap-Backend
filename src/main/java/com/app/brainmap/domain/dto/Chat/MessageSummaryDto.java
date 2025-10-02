package com.app.brainmap.domain.dto.Chat;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MessageSummaryDto {
    private UUID id;
    private String name;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
