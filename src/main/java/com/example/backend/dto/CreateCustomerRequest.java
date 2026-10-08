package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCustomerRequest(
        String name,
        @NotBlank(message = "El teléfono es obligatorio") String phone,
        String email,
        @NotNull(message = "El merchantId es obligatorio") Long merchantId) {
}
