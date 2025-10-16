// filepath: /home/axob12/Desktop/BrainMap/brainMap-Backend/src/main/java/com/app/brainmap/domain/dto/NotificationResponseDto.java
package com.app.brainmap.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class NotificationResponseDto {
    private UUID id;
    private String title;
    private String body;
    private String type;
    private String data;
    private Boolean isRead;
    private LocalDateTime createdAt;
}

