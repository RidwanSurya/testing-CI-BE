package com.example.wandoor.model.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(
        @NotBlank
        String user_id,

        @NotBlank
        String otp_code
) {
}
