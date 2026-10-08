package com.example.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductVariantRequest(
        @NotBlank(message = "El SKU es obligatorio") String sku,
        String size,
        String color,
        @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.0", message = "El precio no puede ser negativo") BigDecimal price,
        @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser negativo") Integer stock,
        @NotNull(message = "El productId es obligatorio") Long productId,
        @NotNull(message = "El merchantId es obligatorio") Long merchantId
    ) {
}
