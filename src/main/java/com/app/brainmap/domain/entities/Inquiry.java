package com.app.brainmap.domain.entities;

import com.app.brainmap.domain.InquiryStatus;
import com.app.brainmap.domain.InquiryType;
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

    @ManyToOne
    @JoinColumn(name = "resolver_id", foreignKey = @ForeignKey(name = "fk_inquiry_resolver", foreignKeyDefinition = "FOREIGN KEY (resolver_id) REFERENCES users(id) ON DELETE SET NULL"))
    private User resolver;

    @Enumerated(EnumType.STRING)
    private InquiryType inquiryType;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String inquiryContent;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    private String ResponseContent;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if(this.status == null) {
            this.status = InquiryStatus.PENDING; // Default status
        }
    }
}
