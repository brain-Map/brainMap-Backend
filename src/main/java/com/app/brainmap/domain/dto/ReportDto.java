package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.entities.ReportPriority;
import com.app.brainmap.domain.entities.ReportStatus;
import com.app.brainmap.domain.entities.ReportType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ReportCreateDTO {
    private ReportType reportType;
    private String reportedUsername;
    private String title;
    private String description;
    private List<String> evidenceFiles;
}

@Data
public class ReportResponseDTO {
    private UUID reportId;
    private String reporterName;
    private String reportedUserName;
    private ReportType reportType;
    private String title;
    private String description;
    private ReportStatus status;
    private ReportPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private String resolution;
    private List<String> evidenceFiles;
}