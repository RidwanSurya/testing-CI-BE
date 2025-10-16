package com.example.wandoor.model.entity;

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
public class SplitBillMember {
    private String id;
    private String splitBillId;
    private String userId;
    private Number amountShare;
    private Boolean hasPaid;
    private LocalDateTime paymentDate;
    private String memberName;
    private Boolean isDeleted;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;

}
