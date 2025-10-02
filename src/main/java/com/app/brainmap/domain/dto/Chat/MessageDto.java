package com.app.brainmap.domain.dto.Chat;

import lombok.*;

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
