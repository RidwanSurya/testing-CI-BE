package com.example.wandoor.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseTest(
        @JsonProperty("status") boolean status,
        @JsonProperty("message") String message,
        @JsonProperty("user_id") String userId
) {
}
