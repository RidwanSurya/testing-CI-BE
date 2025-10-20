package com.example.wandoor.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)

public class SplitBill {
    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String cif;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String splitBillTitle;

    @Column(nullable = true)
    private String currency = "IDR";

    @Column(nullable = false)
    private Number totalAmount;

    @Column(nullable = true)
    private Boolean hasPaid = false;

    @Column(nullable = false)
    private LocalDateTime paymentTime;

    @Column(nullable=false)
    private Boolean is_deleted = false;

    @Column(nullable = false)
    private String createdBy = "SYSTEM";

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private String updatedBy = "SYSTEM";

    @Column(nullable = false)
    private LocalDateTime updatedTime;
}
