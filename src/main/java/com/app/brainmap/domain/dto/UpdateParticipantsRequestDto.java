package com.app.brainmap.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateParticipantsRequestDto {

    @NotBlank(message = "Action is required")
    private String action; // "join" or "leave"

    @NotNull(message = "User ID is required")
    private UUID userId;
}
