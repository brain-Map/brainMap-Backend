package com.app.brainmap.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMeetingRequestDto {

    @NotBlank(message = "Meeting title is required")
    private String title;
}
