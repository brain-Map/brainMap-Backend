package com.app.brainmap.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertRequest {
    private UUID id; // Domain expert ID
    private String firstName;
    private String lastName;
    private String email;
    private String domain;
    private String experience;
    private String status; // Expert status (UNVERIFIED, VERIFIED, REJECTED)
    private LocalDateTime submittedAt;
    
    // Verification documents information
    private List<VerificationDocumentInfo> documents;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationDocumentInfo {
        private UUID documentId;
        private String fileName;
        private String fileUrl;
        private String contentType;
        private Long size;
        private String status; // Document status (PENDING, APPROVED, REJECTED)
        private LocalDateTime uploadedAt;
    }
}