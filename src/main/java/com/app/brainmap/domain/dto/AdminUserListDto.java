package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserListDto {
    private UUID id;
    private String username;
    private String email;
    private String mobileNumber;
    private UserRoleType userRole;
    private UserStatus status;
    private String createdAt;
}
