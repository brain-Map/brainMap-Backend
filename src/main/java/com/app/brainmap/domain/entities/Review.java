package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id", updatable = false, nullable = false)
    private UUID reviewId;

    @Column(name = "rate", nullable = false)
    private Integer rate;

    @Column(name = "review", nullable = false)
    private String review;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDate createdDate;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalTime createdTime;


    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private User member;



}
