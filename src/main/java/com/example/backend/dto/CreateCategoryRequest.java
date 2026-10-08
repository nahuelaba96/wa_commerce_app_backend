package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio") String name,
        @NotNull(message = "El merchantId es obligatorio") Long merchantId) {
}
