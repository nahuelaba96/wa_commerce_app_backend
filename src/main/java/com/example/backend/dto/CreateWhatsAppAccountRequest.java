package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

public record CreateWhatsAppAccountRequest(@NotNull(message = "El merchantId es obligatorio") Long merchantId) {
}
