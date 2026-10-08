package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMerchantRequest(
        @NotBlank(message = "El nombre del comercio es obligatorio") String name,
        @NotNull(message = "El ownerUserId es obligatorio") Long ownerUserId
) {

}
