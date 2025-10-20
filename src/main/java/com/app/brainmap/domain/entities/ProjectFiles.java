package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    @Column(name = "file_url", nullable = false)
    private String url;

}
