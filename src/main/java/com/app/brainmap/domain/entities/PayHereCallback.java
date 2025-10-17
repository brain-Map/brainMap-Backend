package com.app.brainmap.domain.entities;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "payhere_callbacks", indexes = {
    @Index(name = "idx_payhere_callbacks_order_id", columnList = "order_id"),
    @Index(name = "idx_payhere_callbacks_payment_id", columnList = "payment_id"),
    @Index(name = "idx_payhere_callbacks_processed", columnList = "processed")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PayHereCallback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_session_id")
    private PaymentSession paymentSession;
    
    @Column(name = "order_id", nullable = false)
    private String orderId;
    
    @Column(name = "payhere_order_id")
    private String payHereOrderId;
    
    @Column(name = "merchant_id")
    private String merchantId;
    
    @Column(name = "payment_id")
    private String paymentId;
    
    // PayHere callback data
    @Column(name = "status_code")
    private String statusCode;
    
    @Column(name = "md5sig")
    private String md5Sig;
    
    @Column(name = "method")
    private String method;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 3)
    private String currency;
    
    // Raw callback data
    @Type(JsonType.class)
    @Column(name = "raw_data", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> rawData;
    
    // Processing status
    @Builder.Default
    @Column(nullable = false)
    private Boolean processed = false;
    
    @Column(name = "processing_error", columnDefinition = "TEXT")
    private String processingError;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
