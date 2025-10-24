package com.example.wandoor.model.request;
import jakarta.validation.constraints.NotBlank;

public record SplitBillDetailRequest(
    @NotBlank
    String split_bill_id,

    @NotBlank
    String transaction_id
    ) {
}
