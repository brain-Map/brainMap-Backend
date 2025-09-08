package com.app.brainmap.domain.dto.DomainExpert;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteDomainExpertProfileDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String dateOfBirth;
    private String address;
    private String location;
    private String gender;
    private String mentorshipType;
    private String availability;
    private String hourlyRate;
    private String maxMentees;
    private String bio;
    private String workExperience;
    private String linkedinProfile;
    private String portfolio;
    private List<ExpertiseAreaDto> expertiseAreas;
    private List<EducationDto> education;
    private MultipartFile profilePhoto;
    private List<MultipartFile> verificationDocs;
}
