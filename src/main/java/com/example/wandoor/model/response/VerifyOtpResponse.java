package com.example.wandoor.model.response;

public record VerifyOtpResponse(
        boolean status,
        String message,
        String token,
        User user
) {

    public record User (
            String userId,
            String username,
            String role
    ) {}
}
