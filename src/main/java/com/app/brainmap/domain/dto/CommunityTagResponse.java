package com.app.brainmap.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityTagResponse {

    private UUID id;
    private String name;
    private Integer postCount;
}
