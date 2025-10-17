package com.app.brainmap.domain.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserByAdminResDto {
    private String username;
    private String email;
    private String userRole;
    private Boolean conformed;
}
