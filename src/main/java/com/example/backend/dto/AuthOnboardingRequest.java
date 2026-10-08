package com.example.backend.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthOnboardingRequest(
        @NotNull UUID authUserId,
        @NotBlank @Email String email,
        @NotBlank String personalName,
        @NotBlank String merchantName) {
}
