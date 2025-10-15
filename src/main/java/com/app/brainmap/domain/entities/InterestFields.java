package com.app.brainmap.domain.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "interest_fields")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InterestFields {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "field_id" , nullable = false , updatable = false)
    private UUID id;

    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
