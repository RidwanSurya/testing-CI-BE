package com.example.wandoor.model.response;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RecordBuilder
public record DplkListResponse(
        String status,
        String message,
        List<Data> data

) {

    @RecordBuilder
    public record Data(
            String title,
            BigDecimal totalBalance,
            List<Items> items
    ){

        @RecordBuilder
        public record Items(
                String fundId,
                String depositAccountNumber,
                BigDecimal balance,
                LocalDateTime maturityDate,
                String currencyCode,
                Double growth,
                BigDecimal accumulatedBalance
        ){
        }
    }

}
