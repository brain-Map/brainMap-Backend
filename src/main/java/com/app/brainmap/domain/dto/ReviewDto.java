package com.app.brainmap.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewDto(
    UUID reviewId,
    Integer rate,
    String review,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    UUID memberId,
    UUID mentorId,
    UUID bookedId
) {}