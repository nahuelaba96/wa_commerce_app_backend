package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateProductRequest(

        @NotBlank(message = "El nombre del producto es obligatorio")
        String name,

        String description,

        String brand,

        @NotNull(message = "El merchantId es obligatorio")
        Long merchantId,

        @NotNull(message = "La categoría del producto es obligatoria")
        Long categoryId,

        List<CreateProductVariantRequestFromProduct> variants

) {
}