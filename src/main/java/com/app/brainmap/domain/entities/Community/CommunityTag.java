<<<<<<< HEAD:src/main/java/com/app/brainmap/domain/entities/CommunityTag.java
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
=======
package com.app.brainmap.domain.entities.Community;

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
>>>>>>> 8927c80eb6a121132779d0a641aeb7caad6a320c:src/main/java/com/app/brainmap/domain/entities/Community/CommunityTag.java
}