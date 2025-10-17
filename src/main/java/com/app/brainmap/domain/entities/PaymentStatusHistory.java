package com.app.brainmap.domain.entities;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "payment_status_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PaymentStatusHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_session_id", nullable = false)
    private PaymentSession paymentSession;
    
    @Column(name = "previous_status", length = 20)
    private String previousStatus;
    
    @Column(name = "new_status", nullable = false, length = 20)
    private String newStatus;
    
    @Column(name = "changed_by", length = 100)
    private String changedBy;
    
    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;
    
    @Type(JsonType.class)
    @Column(name = "payhere_data", columnDefinition = "jsonb")
    private Map<String, Object> payHereData;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
