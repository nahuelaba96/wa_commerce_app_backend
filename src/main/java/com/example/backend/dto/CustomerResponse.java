package com.example.backend.dto;

public record CustomerResponse(Long id, String name, String phone, String email, Long merchantId) {
}
