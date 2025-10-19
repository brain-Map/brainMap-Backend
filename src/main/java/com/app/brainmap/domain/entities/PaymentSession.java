package com.app.brainmap.domain.entities;

import com.app.brainmap.domain.PaymentStatus;
import com.app.brainmap.domain.entities.DomainExpert.ServiceBooking;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "payment_sessions", indexes = {
    @Index(name = "idx_payment_sessions_payment_id", columnList = "payment_id"),
    @Index(name = "idx_payment_sessions_order_id", columnList = "order_id"),
    @Index(name = "idx_payment_sessions_user_id", columnList = "user_id"),
    @Index(name = "idx_payment_sessions_status", columnList = "status")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PaymentSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "payment_id", unique = true, nullable = false)
    private String paymentId;
    
    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 3, nullable = false)
    @Builder.Default
    private String currency = "LKR";
    
    @Column(name = "item_description", columnDefinition = "TEXT")
    private String itemDescription;
    
    // Customer information
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;
    
    @Column(name = "customer_phone")
    private String customerPhone;
    
    @Column(name = "customer_address", columnDefinition = "TEXT")
    private String customerAddress;
    
    private String city;
    
    @Builder.Default
    private String country = "Sri Lanka";
    
    // PayHere configuration
    @Column(name = "payhere_mode", nullable = false)
    private String payHereMode;
    
    @Column(name = "payhere_merchant_id", nullable = false)
    private String payHereMerchantId;
    
    @Column(name = "payhere_hash")
    private String payHereHash;
    
    // Status tracking
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private ServiceBooking serviceBooking;
}
