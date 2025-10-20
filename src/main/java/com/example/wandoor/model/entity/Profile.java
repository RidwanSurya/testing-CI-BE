package com.example.wandoor.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Profile {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String cif;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    private String lastName;

    private LocalDateTime dob;

    @Column(name = "phone_number", nullable = false, precision = 15, scale = 0)
    private BigDecimal phoneNumber;

    @Column(nullable = false)
    private String emailAddress;

    @Column(nullable = false)
    private String nik;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private String updatedBy;

    @Column(nullable = false)
    private LocalDateTime updatedTime;




}
