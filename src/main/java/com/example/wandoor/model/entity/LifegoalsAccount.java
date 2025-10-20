package com.example.wandoor.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
public class LifegoalsAccount {
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
    private String lifegoalsName;

    @Column(nullable = false)
    private String lifegoalsCategoryName;

    @Column(nullable = false)
    private BigDecimal accountDeposit;

    @Column(nullable = false)
    private BigDecimal accountTarget;

    @Column(nullable = false)
    private String lifegoalsTrxCreationId;

    @Column(nullable = false)
    private BigDecimal accountTargetAmount;

    @Column(nullable = false)
    private BigDecimal estimationAmount;

    @Column(nullable = false)
    private String lifegoalsDescription;

    @Column(nullable = false)
    private LocalDateTime maturityDate;

    @Column(nullable = false)
    private LocalDateTime lifegoalsDuration;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private String updatedBy;

    @Column(nullable = false)
    private LocalDateTime updatedTime;
}
