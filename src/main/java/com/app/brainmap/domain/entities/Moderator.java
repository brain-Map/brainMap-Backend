package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

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
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id") // uses same column as primary key
    private User user;




}
