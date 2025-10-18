package com.app.brainmap.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNotesRequestDto {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 200, message = "Title cannot exceed {max} characters")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Size(max = 5000, message = "Description cannot exceed {max} characters")
    private String description;
} 