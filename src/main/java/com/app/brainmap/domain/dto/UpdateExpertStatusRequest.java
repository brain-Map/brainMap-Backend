package com.app.brainmap.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExpertStatusRequest {
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "APPROVED|PENDING|REJECTED", 
             message = "Status must be one of: APPROVED, PENDING, REJECTED")
    private String status;
    
    private String reviewNotes; // Optional field for review notes (rejection reason, etc.)
}