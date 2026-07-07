package com.api.xpress.airtime.data.models;

import com.api.xpress.auth_config.user.data.models.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "airtime_purchase")
@EntityListeners(AuditingEntityListener.class)
public class AirtimePurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, updatable = false)
    private String uniqueCode;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionTime;
}
