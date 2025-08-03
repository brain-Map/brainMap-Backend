package com.app.brainmap.domain;

import com.app.brainmap.domain.dto.SocialLinkDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUser {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private Set<SocialLinkDto> socialLinks = new HashSet<>();
    private Date dateOfBirth;
    private String city;
    private String availability;
    private String experience;
    private String gender;
    private String qualification;
    private String bio;
}
