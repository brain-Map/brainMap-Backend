package com.app.brainmap.domain.entities;

import com.app.brainmap.domain.ProjectCollaboratorAccept;
import com.app.brainmap.domain.ProjectPositionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @JsonIgnore  // stop recursion her
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;


    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectPositionType role;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProjectCollaboratorAccept status;

    // ✅ Convenience constructor
    public UserProject(User user, Project project,
                       ProjectPositionType role,
                       ProjectCollaboratorAccept status) {
        this.id = new UserProjectCompositeKey(user.getId(), project.getId());
        this.user = user;
        this.project = project;
        this.role = role;
        this.status = status;
    }
}
