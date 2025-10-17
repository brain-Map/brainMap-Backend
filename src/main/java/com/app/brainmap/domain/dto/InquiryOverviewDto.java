package com.app.brainmap.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InquiryOverviewDto {
    private Long totalInquiries;
    private Long pending;
    private Long resolved;
    private Long reviewed;
}

