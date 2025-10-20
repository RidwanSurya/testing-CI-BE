package com.example.wandoor.repository;

import com.example.wandoor.model.entity.TrxHistory;
import com.example.wandoor.model.enums.TrxType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TrxHistoryRepository extends JpaRepository<TrxHistory, String> {
    @Query("""
        SELECT COALESCE(SUM(t.trxAmount), 0)
        FROM TrxHistory t
        WHERE t.userId = :userId
          AND t.trxType = :trxType
    """)
    BigDecimal sumTrxAmountByUserIdAndCifAndTrxType(
            @Param("userId") String userId,
//            @Param("cif") String cif,
            @Param("trxType") TrxType trxType
    );
}
