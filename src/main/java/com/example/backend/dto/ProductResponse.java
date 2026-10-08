package com.example.backend.dto;

import java.util.List;

public record ProductResponse(
    Long id, 
    String name, 
    String description, 
    String brand, 
    Long merchantId,
    List<ProductVariantResponse> variants,
    CategoryResponse category) {
}
