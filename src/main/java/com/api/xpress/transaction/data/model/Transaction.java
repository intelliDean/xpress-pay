package com.api.xpress.transaction.data.model;

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
@Table(name = "transaction")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}