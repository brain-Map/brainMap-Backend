package com.app.brainmap.domain.entities;

import com.app.brainmap.domain.ProjectPositionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_project")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserProject {

    @EmbeddedId
    private UserProjectCompositeKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "position", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectPositionType position;
}
