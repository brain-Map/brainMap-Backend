package com.app.brainmap.domain.entities;

import com.app.brainmap.domain.CommunityPostType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "community_posts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommunityPost {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID communityPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private CommunityPostType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityComment> comments = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "community_post_tags",
            joinColumns = @JoinColumn(name = "community_post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<CommunityTag> tags = new HashSet<>();

    @PrePersist
    protected void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CommunityPost that = (CommunityPost) o;
        return Objects.equals(communityPostId, that.communityPostId) && Objects.equals(author, that.author) && Objects.equals(title, that.title) && Objects.equals(content, that.content) && type == that.type && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(comments, that.comments) && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(communityPostId, author, title, content, type, createdAt, updatedAt, comments, tags);
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
