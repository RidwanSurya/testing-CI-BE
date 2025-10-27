package com.example.wandoor.model.response;

import com.example.wandoor.model.enums.DebitCredit;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RecordBuilder
public record TrxResponse(
        String transactionId,
        LocalDateTime transactionDate,
        String transactionType,
        String debitCredit,
        String partyName,
        String partyDetail,
        BigDecimal amount
) {
}
