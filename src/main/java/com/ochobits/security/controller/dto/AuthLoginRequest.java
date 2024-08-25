package com.ochobits.security.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record AuthLoginRequest(

        @JsonProperty("username")
        @NotBlank String userName,
        @NotBlank String password
) {
}
