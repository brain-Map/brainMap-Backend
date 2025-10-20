package com.app.brainmap.domain.dto.DomainExpert;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDocumentDto {
    private UUID id;
    private String fileName;
    private String fileUrl;
    private String contentType;
    private Long size;
    private String status;
    private LocalDateTime uploadedAt;
    private String reviewNotes;
}
