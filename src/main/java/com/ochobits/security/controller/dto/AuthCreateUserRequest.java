package com.ochobits.security.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record AuthCreateUserRequest(
        @JsonProperty("username")

        @NotBlank String userName,
        @NotBlank String password,

        @JsonProperty("roles")
        @Valid AuthCreateRoleRequest roleRequest
) {
}
