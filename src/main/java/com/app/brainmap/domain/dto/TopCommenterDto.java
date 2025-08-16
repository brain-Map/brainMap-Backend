package com.app.brainmap.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCommenterDto {
    private UUID id;
    private String name;
    private String avatar; // optional
    private Long commentCount;
    private String role; // optional
}
