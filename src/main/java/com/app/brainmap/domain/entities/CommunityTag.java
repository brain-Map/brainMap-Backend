package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "community_tags")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommunityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tagId;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<CommunityPost> posts = new HashSet<>();
}