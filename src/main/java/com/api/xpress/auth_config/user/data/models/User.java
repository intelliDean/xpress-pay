package com.api.xpress.auth_config.user.data.models;

import com.api.xpress.auth_config.user.data.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true,  nullable = false)
    private String emailAddress;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean enabled = false;
}