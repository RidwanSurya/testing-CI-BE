package com.example.wandoor.repository;

import com.example.wandoor.model.entity.TrxHistory;
import com.example.wandoor.model.enums.DebitCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface TrxHistoryRepository extends JpaRepository<TrxHistory, String> {
    @Query("""
        SELECT COALESCE(SUM(t.transactionAmount), 0)
        FROM TrxHistory t
        WHERE t.userId = :userId
          AND t.debitCredit = :debitCredit
    """)
    BigDecimal sumTransactionAmountByUserIdAndCifAndDebitCredit(
            @Param("userId") String userId,
//            @Param("cif") String cif,
            @Param("debitCredit") DebitCredit debitCredit
    );
}
