package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "services")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "service_id", updatable = false, nullable = false)
    private UUID serviceId;

    @Column(name = "title",columnDefinition = "TEXT",  nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;


    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;



}
