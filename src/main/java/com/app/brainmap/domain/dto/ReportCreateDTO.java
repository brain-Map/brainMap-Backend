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

