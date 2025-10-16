package com.app.brainmap.domain.dto.Chat;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateGroupRequestDto {
    private String name;
    private UUID projectId;
    private List<UUID> members;
}

