package com.app.brainmap.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDashboardStatusDto {
    private long userCount;
    private long activeProjects;
    private long pendingDomainExperts;
}