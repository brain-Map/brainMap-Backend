package com.app.brainmap.domain.dto.DomainExpert;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationDto {
    private String degree;
    private String school;
    private String year;
}
