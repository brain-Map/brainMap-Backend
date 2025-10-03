// Updated file: src/main/java/com/app/brainmap/domain/entities/Message.java
package com.app.brainmap.domain.entities.Chat;

import com.app.brainmap.domain.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User senderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiverId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime timestamp;

    private String status;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}