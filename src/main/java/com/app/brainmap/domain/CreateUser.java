package com.app.brainmap.domain;

import com.app.brainmap.domain.dto.SocialLinkDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUser {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private UserRoleType userRole;
    private Set<SocialLinkDto> socialLinks = new HashSet<>();

}
