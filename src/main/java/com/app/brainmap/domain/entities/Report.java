package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reports")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "report_id", updatable = false, nullable = false)
    private UUID reportId; // Fixed typo from "reprottId"

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description; // Changed from "report" to "description"

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private ReportPriority priority = ReportPriority.MEDIUM;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // Changed from "member"

    @ManyToOne
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser; // Changed from "moderator"

    @ElementCollection
    @CollectionTable(name = "report_evidence_files", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "file_url")
    private List<String> evidenceFiles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}

enum ReportType {
    TECHNICAL_ISSUE, USER_COMPLAINT, HARASSMENT, PAYMENT_ISSUE, SERVICE_QUALITY, OTHER
}

enum ReportStatus {
    OPEN, IN_PROGRESS, RESOLVED, CLOSED
}

enum ReportPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}