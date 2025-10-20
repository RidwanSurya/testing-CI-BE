package com.example.wandoor.repository;

import com.example.wandoor.model.entity.SplitBillMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface SplitBillMemberRepository extends JpaRepository<SplitBillMember, String> {
    // Remaining = jumlah porsi yang belum lunas pada SEMUA bill milik si creator
    @Query("""
        SELECT COALESCE(SUM(m.amountShare), 0)
        FROM SplitBillMember m
        JOIN m.splitBill sb
        WHERE sb.userId = :userId
          AND sb.cif = :cif
          AND sb.isDeleted = 0
          AND m.isDeleted = 0
          AND m.hasPaid = 0
    """)
    BigDecimal sumRemainingForCreator(@Param("userId") String userId, @Param("cif") String cif);
}
