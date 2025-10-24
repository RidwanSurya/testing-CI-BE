package com.example.wandoor.model.response;
import java.math.BigDecimal;
import java.util.List;

public record SplitBillDetailResponse (
    String status,
    String message,
    Data data
) {
    public record Data(
        String split_bill_id,
        String split_bill_title,
        String currency,
        BigDecimal total_bill,
        String created_time,
        String transaction_id,
        List<Member> members
    ) {
        public record Member(
            String member_name,
            BigDecimal amount,
            String status,
            String payment_time
        ) {
        }
    }
}
