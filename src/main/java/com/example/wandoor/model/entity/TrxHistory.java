package com.example.wandoor.model.entity;

import com.example.wandoor.model.enums.DebitCredit;
import jakarta.persistence.*;
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
public class TrxHistory {
    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = false)
    private BigDecimal transactionAmount;

    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false)
    private String transactionDescription;

    @Column(nullable = false)
    private String partyName;

    @Column(nullable = false)
    private String partyDetail;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "debit_credit",nullable = false)
    private DebitCredit debitCredit;

    @Column(nullable = false)
    private String splitBillId;

    @Column(nullable = false)
    private String refId;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private String updatedBy;

    @Column(nullable = false)
    private LocalDateTime updatedTime;

}