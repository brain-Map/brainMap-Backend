package com.app.brainmap.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserDto {
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

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
