package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

public record CreateProductImageRequest(@NotNull(message = "El productId es obligatorio") Long productId) {
}
