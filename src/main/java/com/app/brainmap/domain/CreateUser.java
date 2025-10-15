package com.app.brainmap.domain;

import com.app.brainmap.domain.dto.SocialLinkDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUser {

    private UUID userId;
    private String username;
    private String email;
    private UserRoleType userRole;
}
