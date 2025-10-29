package com.example.wandoor.model.response;

public record ResendOtpResponse(
        boolean status,
        String message,
        int resendCooldown
) {
}
