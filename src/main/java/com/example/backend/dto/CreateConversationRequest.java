package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

public record CreateConversationRequest(
        @NotNull(message = "El merchantId es obligatorio") Long merchantId,
        @NotNull(message = "El customerId es obligatorio") Long customerId,
        Long whatsappAccountId) {
}
