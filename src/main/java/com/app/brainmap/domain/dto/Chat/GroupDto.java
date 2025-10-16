package com.app.brainmap.domain.dto.Chat;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GroupDto {
    private UUID id;
    private String name;
    private List<UUID> memberIds;
}