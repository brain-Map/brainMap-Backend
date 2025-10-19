package com.app.brainmap.domain.dto;

import java.util.UUID;

public record ProjectFileDto(
        UUID id,
        UUID projectId,
        String url

) {
}
