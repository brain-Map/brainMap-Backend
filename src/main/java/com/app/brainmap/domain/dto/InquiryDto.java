package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.InquiryStatus;
import com.app.brainmap.domain.InquiryType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InquiryDto {
    private UUID inquiryId;
    private UUID userId;
    private UUID resolver;
    private InquiryType inquiryType;
    private String title;
    private String inquiryContent;
    private InquiryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String ResponseContent;
}
