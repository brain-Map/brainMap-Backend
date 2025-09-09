package com.app.brainmap.domain.dto.DomainExpert;

import lombok.*;

import java.time.LocalDateTime;

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
//    private String status;


}
