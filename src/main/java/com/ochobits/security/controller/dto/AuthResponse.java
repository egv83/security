package com.ochobits.security.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"userName","messaje", "jwt","status"})
public record AuthResponse(

        @JsonProperty("username")
        String userName,
        String message,
        String jwt,
        boolean status
) {
}
