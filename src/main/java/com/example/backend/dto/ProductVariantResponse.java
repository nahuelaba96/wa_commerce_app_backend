package com.example.backend.dto;

import java.math.BigDecimal;

public record ProductVariantResponse(Long id, String sku, String size, String color, BigDecimal price, Long productId, Integer stock) {
}
