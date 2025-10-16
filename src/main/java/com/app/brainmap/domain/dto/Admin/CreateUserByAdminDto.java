package com.app.brainmap.domain.dto.Admin;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserByAdminDto {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "User Role cannot be empty")
    private String userRole;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}