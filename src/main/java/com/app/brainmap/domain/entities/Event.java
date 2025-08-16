package com.app.brainmap.domain.entities;

    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.util.UUID;

    @Entity
    @Table(name = "events")
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder(toBuilder = true)
    public class Event {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "event_id", updatable = false, nullable = false)
        private UUID eventId;

        @Column(name = "title", nullable = false)
        private String title;

        @Column(name = "description", columnDefinition = "TEXT", nullable = false)
        private String description;

        @Column(name = "created_date", nullable = false, updatable = false)
        private LocalDate createdDate;

        @Column(name = "due_date")
        private LocalDate dueDate;

        @Column(name = "due_time")
        private LocalTime dueTime;

        @Column(name = "created_time", nullable = false, updatable = false)
        private LocalTime createdTime;

        @ManyToOne(optional = false)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;
    }