package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.UserRoleType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserTrendDto {
    private int year;
    private String month;
    private int modaratorCount;
    private int mentorCount;
    private int projectMemberCount;
}
