package com.example.wandoor.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VerifyOtpResponseTest(
        @JsonProperty("status") boolean status,
        @JsonProperty("message") String message,
        @JsonProperty("token") String token,
        @JsonProperty("user") User user
) {
    public record User(String userId, String username, String role) {}
}
