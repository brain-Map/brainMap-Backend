package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.UserRoleType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserDto {

    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "User Role cannot be empty")
    private String userRole;

    private String mobileNumber;
    private Date dateOfBirth;
    private Set<SocialLinkDto> socialLinks;
    private String city;
    private String availability;
    private String experience;
    private String gender;
    private String qualification;
    private String bio;
}
