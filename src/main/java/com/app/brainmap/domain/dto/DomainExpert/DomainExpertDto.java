// ...existing code...
package com.app.brainmap.domain.dto.DomainExpert;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.app.brainmap.domain.dto.UserSocialLinkDto;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainExpertDto {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String avatar;
    private String profilePhotoUrl;
    private String status;
    private String domain;
    private String bio;
    private String workExperience;
    private String linkedinProfile;
    private String portfolio;
    private String location;
    private LocalDateTime createdAt;

    private List<ExpertiseAreaDto> expertiseAreas;
    private List<EducationDto> educations;
    private List<ServiceListingResponseDto> services;
    private List<UserSocialLinkDto> socialLinks;

    private Double rating; // average rating
    private Long reviewsCount;
    private Long completedBookingsCount;
}
