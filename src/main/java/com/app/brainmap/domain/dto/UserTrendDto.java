package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.UserRoleType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserTrendDto {
    private String month;
    private UserRoleType userRole;
    private Long count;
}
