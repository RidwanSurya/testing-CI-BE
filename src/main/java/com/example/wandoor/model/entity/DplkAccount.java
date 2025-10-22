package com.example.wandoor.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DplkAccount {
    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String cif;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountNumberDplk;

    @Column(nullable = false)
    private String currenyCode;

    @Column(nullable = false)
    private String dplkProductName;

    @Column(nullable = false)
    private BigDecimal dplkInitialDeposit;

    @Column(nullable = false)
    private String produkType;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime cratedTime;

    @Column(nullable = false)
    private String updatedBy;

    @Column(nullable = false)
    private LocalDateTime updatedTime;

}
