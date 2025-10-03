package com.app.brainmap.domain.dto;

    import lombok.*;
    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.util.UUID;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public final class EventDto {
        private UUID eventId;
        private String title;
        private String description;
        private LocalDate createdDate;
        private LocalDate dueDate;
        private LocalTime dueTime;
        private LocalTime createdTime;
        private UUID userId;
    }