package com.example.backend.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.dto.AuthOnboardingRequest;
import com.example.backend.dto.AuthOnboardingResponse;
import com.example.backend.service.MerchantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/internal/auth")
public class AuthOnboardingController {
    private final MerchantService merchantService;
    private final String internalServiceToken;

    public AuthOnboardingController(
            MerchantService merchantService,
            @Value("${INTERNAL_SERVICE_TOKEN:}") String internalServiceToken) {
        this.merchantService = merchantService;
        this.internalServiceToken = internalServiceToken;
    }

    @PostMapping("/onboarding")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthOnboardingResponse createOwner(
            @RequestHeader("X-Internal-Service-Token") String suppliedToken,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody AuthOnboardingRequest request) {
        if (internalServiceToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Internal onboarding is not configured");
        }
        if (!MessageDigest.isEqual(
                suppliedToken.getBytes(StandardCharsets.UTF_8),
                internalServiceToken.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid service credentials");
        }
        if (!request.authUserId().toString().equals(idempotencyKey)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Idempotency key must match authUserId");
        }
        return merchantService.createAuthOwner(request);
    }
}
