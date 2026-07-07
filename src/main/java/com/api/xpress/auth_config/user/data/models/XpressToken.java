package com.api.xpress.auth_config.user.data.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "xpress_tokens")
@EntityListeners(AuditingEntityListener.class)
public class XpressToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    @Column(columnDefinition = "boolean default false")
    private boolean revoked;

    @Column(columnDefinition = "boolean default false")
    private boolean expired;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
