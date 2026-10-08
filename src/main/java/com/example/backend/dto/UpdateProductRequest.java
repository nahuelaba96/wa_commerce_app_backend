package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProductRequest(
        @NotBlank(message = "El nombre del producto es obligatorio")
        String name,

        String description,

        String brand,

        @NotNull(message = "La categoría del producto es obligatoria")
        Long categoryId
) {
}
