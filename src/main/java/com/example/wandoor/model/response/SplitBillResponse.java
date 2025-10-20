package com.example.wandoor.model.response;


import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.LocalDateTime;

@RecordBuilder
public record SplitBillResponse(
        String splitBillId,
        String transactionId,
        String splitBillTitle,
        Number totalMembers,
        Number paidAmount,
        Number unpaidAmount,
        Number totalBillAmount,
        LocalDateTime createdTime,
        Number unpaidCount
) {}
