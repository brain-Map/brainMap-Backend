package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "replies")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reply_id", updatable = false, nullable = false)
    private UUID replyId;

    @Column(name = "reply", nullable = false, columnDefinition = "TEXT")
    private String reply;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



}
