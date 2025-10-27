package com.example.wandoor.model.response;
import java.math.BigDecimal;
import java.util.List;

public record SplitBillDetailResponse (
    String status,
    String message,
    Data data
) {
    public record Data(
        String splitBillId,
        String splitBillTitle,
        String currency,
        BigDecimal totalBill,
        String createdTime,
        String transactionId,
        List<Member> members
    ) {
        public record Member(
            String memberId,
            String memberName,
            BigDecimal amount,
            String status,
            String paymentTime,
            Boolean hasPaid
        ) {
        }
    }
}
