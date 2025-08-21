package com.app.brainmap.domain.dto;

import java.util.UUID;

public record UserImageDto(
        UUID userId,
        String avatar
) {
}
