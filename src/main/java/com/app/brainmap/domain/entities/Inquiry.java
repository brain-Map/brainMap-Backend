package com.app.brainmap.domain.entities;

import com.app.brainmap.domain.InquiryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inquiries")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID inquiryId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if(this.status == null) {
            this.status = InquiryStatus.PENDING; // Default status
        }
    }
}
