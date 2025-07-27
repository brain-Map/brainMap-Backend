package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "moderators")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Moderator {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID Id;



}
