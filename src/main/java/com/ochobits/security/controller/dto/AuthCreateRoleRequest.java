package com.ochobits.security.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public record AuthCreateRoleRequest(
        @JsonProperty("rolesname")
        @Size(max = 3, message = "The user can´t have more than 3 roles") List<String> roleListName  /*SE VALIDA QUE SOLO SE ENVIE 3 ROLES COMO MAXIMO*/
) {
}
