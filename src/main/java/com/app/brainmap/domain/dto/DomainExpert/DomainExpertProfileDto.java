package com.app.brainmap.domain.dto.DomainExpert;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainExpertProfileDto {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String email;
    private String phone;
    private String dateOfBirth;
    private String location;
    private String gender;
    private String bio;
    private String workExperience;
    private String linkedinProfile;
    private String portfolio;
    private List<ExpertiseAreaDto> expertiseAreas;
    private List<EducationDto> education;

    // Files / urls
    private String profilePhotoUrl;
    private List<VerificationDocumentDto> verificationDocs;

}
