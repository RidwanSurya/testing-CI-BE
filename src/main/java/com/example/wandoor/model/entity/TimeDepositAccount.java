package com.example.wandoor.model.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TimeDepositAccount {

    @Id
    @Column
    private String id;

    @Column
    private String userId;

    @Column
    private String accountType;

    @Column
    private String depositAccountNumber;

    @Column
    private Integer effectiveBalance;

    @Column
    private Integer tenorMonths;

    @Column
    private LocalDateTime maturityDate;

    @Column
    private Double interestRate;

    @Column
    private String depositAccountStatus;
}
