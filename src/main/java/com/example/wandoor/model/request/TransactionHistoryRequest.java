package com.example.wandoor.model.request;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record TransactionHistoryRequest(
        Integer month,
        Integer year,
        String accountNumber
) {
}