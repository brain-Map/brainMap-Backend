package com.app.brainmap.domain.entities.Chat;

import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.domain.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id",
        foreignKey = @ForeignKey(name = "fk_group_project", foreignKeyDefinition = "FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE"))
    private Project project;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "fk_group_members_group", foreignKeyDefinition = "FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE")),
        inverseJoinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_group_members_user", foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
    )
    private Set<User> members;
}
