package com.app.brainmap.domain.dto;

public record DomainExpertsDto(
    String id,
    Integer status,
    String domain,
    String location,
    String rating,
    String about
) {
}
