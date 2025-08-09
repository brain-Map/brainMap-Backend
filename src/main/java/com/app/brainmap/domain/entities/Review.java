package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id", updatable = false, nullable = false)
    private UUID reviewId;

    @Column(name = "rate", nullable = false)
    private Integer rate;

    @Column(name = "review", nullable = false)
    private String review;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "promise_id", nullable = false, foreignKey = @ForeignKey(name = "fk_promise_review", foreignKeyDefinition = "FOREIGN KEY (promise_id) REFERENCES promises(promise_id) ON DELETE CASCADE"))
    private Promise promise;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @PrePersist
    protected void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }


}
