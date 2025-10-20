package com.example.wandoor.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.NumericBooleanConverter;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class OtpVerification {
    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String otpCode;

    @Column(nullable = false)
    private String emailTo;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Integer isUsed;

    @Column(nullable = false)
    private LocalDateTime createdTime;


}
