package com.app.brainmap.domain.entities.DomainExpert;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "domain_expert_verification_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainExpertVerificationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_expert_id")
    private DomainExperts domainExpert;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size_bytes")
    private Long size;

    @Column(name = "status")
    private String status; // PENDING / APPROVED / REJECTED

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes; // Optional notes for review (e.g., rejection reason)

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist(){
        if (uploadedAt == null) uploadedAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }
}

