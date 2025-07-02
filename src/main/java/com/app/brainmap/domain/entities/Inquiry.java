package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String message;

    private String status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
