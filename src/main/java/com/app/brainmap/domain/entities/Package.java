package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "packages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "package_id", updatable = false, nullable = false)
    private UUID packageId;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "number_of_hours", nullable = false)
    private Integer numberOfHours;



    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;


}
