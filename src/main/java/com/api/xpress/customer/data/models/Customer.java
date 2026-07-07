package com.api.xpress.customer.data.models;

import com.api.xpress.auth_config.user.data.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String phoneNumber;

    private BigDecimal balance;
}
