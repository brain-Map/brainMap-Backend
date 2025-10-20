package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "project_members")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProjectMember {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", // uses same column as primary key
        foreignKey = @ForeignKey(name = "fkk7gcnxww33tirr1eke7g5aoq4",
            foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE"))
    private User user;
}
