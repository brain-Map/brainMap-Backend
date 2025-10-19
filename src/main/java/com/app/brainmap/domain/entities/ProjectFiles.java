package com.app.brainmap.domain.entities;

import com.app.brainmap.domain.ProjctStatus;
import com.app.brainmap.domain.ProjectPriority;
import com.app.brainmap.domain.ProjectPrivacy;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "project_files")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProjectFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "file_id" , nullable = false , updatable = false)
    private UUID id;


    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "file_url", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private String url;

}
