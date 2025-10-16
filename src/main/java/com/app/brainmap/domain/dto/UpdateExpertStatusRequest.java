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
    @Pattern(regexp = "VERIFIED|PENDING|UNVERIFIED|REJECTED", 
             message = "Status must be one of: VERIFIED, PENDING, UNVERIFIED, REJECTED")
    private String status;
    
    private String rejectionReason; // Optional field for rejection reason
}