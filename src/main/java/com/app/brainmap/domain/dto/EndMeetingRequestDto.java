package com.app.brainmap.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndMeetingRequestDto {

    @NotNull(message = "Ended by user ID is required")
    private UUID endedBy;
}
