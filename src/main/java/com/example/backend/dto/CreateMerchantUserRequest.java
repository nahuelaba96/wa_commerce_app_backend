package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMerchantUserRequest(
        @NotNull(message = "El merchantId es obligatorio") Long merchantId,
        @NotNull(message = "El userId es obligatorio") Long userId,
        @NotBlank(message = "El rol es obligatorio") String role) {
}
