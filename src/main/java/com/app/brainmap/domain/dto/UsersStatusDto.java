package com.app.brainmap.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersStatusDto {
    private Long totalUsers;
    private Long members;
    private Long domainExperts;
    private Long activeUsers;
}
