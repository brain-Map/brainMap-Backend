package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "socials")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Social {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "social_id", updatable = false, nullable = false)
    private UUID socialId;

    @Column(name = "platform")
    private String platform;

    @Column(name = "link")
    private String link;


    @ManyToOne
    @JoinColumn(name = "expert_id", nullable = false)
    private User expert;



}
