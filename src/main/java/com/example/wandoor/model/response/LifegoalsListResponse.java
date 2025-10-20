package com.example.wandoor.model.response;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.util.List;

@RecordBuilder
public record LifegoalsListResponse(
        String status,
        String message,
        List<Item> item
) {
    @RecordBuilder
    public record Item(
            String goalId,
            String name,
            String subtitle,
            BigDecimal currentSavings,
            BigDecimal target,
            Integer progress
    ){

    }
}
