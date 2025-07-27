package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "promises")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Promise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "promise_id", updatable = false, nullable = false)
    private UUID promiseId;

    @Column(name = "promise_hours", nullable = false)
    private Integer promiseHours;

    @Column(name = "remaining_hours", nullable = false)
    private Integer remainingHours;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDate createdDate;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalTime createdTime;


    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @ManyToOne
    @JoinColumn(name = "expert_id", nullable = false)
    private User expert;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;


}
