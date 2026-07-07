package com.api.xpress.verification_token.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "verification_tokens")
@EntityListeners(AuditingEntityListener.class)
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false, updatable = false)
    private String email;

    @Column(columnDefinition = "boolean default false")
    private boolean revoked;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @Column(nullable = false)
    private LocalDateTime expireAt;

    @Column(columnDefinition = "boolean default false")
    private boolean expired;
}
