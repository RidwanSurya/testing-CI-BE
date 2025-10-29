package com.example.wandoor.model.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(
        @NotBlank
        String sessionId,

        @NotBlank
        String otpCode
) {
}
