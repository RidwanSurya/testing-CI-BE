package com.example.wandoor.model.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Savings {
    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private account_id;
    
}
