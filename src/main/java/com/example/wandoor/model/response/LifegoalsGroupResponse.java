package com.example.wandoor.model.response;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RecordBuilder
public record LifegoalsGroupResponse(
        BigDecimal totalTarget,
        BigDecimal currentBalance,
        List<Item> lifegoalslist
) {
    @RecordBuilder
    public record Item(
            String lifegoalsId,
            String accountNumber,
            String lifegoalsTitle,
            String lifegoalsCategory,
            BigDecimal targetBalance,
            BigDecimal currentBalance,
            String createdTime
    ){}
}
