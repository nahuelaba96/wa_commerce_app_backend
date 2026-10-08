package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

public record CreateMessageRequest(@NotNull(message = "El conversationId es obligatorio") Long conversationId) {
}
