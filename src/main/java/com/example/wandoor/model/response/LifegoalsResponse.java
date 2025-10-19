package com.example.wandoor.model.response;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.util.List;

@RecordBuilder
public record LifegoalsListResponse(
        List<DataLifegoals> dataLifegoalsList
) {

    @RecordBuilder
    public record DataLifegoals(
            String fundId,
            String category,
            BigDecimal totalBalance,
            List<Goals> goals
    ){
        @RecordBuilder
        public record Goals(
                String goalId,
                String name,
                BigDecimal currentSavings,
                BigDecimal target
        ){}
    }
}

