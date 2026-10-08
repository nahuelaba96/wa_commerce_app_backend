package com.example.backend.mapper;

import org.springframework.stereotype.Component;

import com.example.backend.dto.CreateProductVariantRequest;
import com.example.backend.dto.ProductVariantResponse;
import com.example.backend.dto.UpdateProductVariantRequest;
import com.example.backend.entity.ProductVariant;

@Component
public class ProductVariantMapper {
    public ProductVariant toEntity(CreateProductVariantRequest request) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(request.sku());
        variant.setSize(request.size());
        variant.setColor(request.color());
        variant.setPrice(request.price());
        variant.setStock(request.stock());
        return variant;
    }

    public void updateEntity(ProductVariant variant, UpdateProductVariantRequest request) {
        variant.setSku(request.sku());
        variant.setSize(request.size());
        variant.setColor(request.color());
        variant.setPrice(request.price());
        variant.setStock(request.stock());
    }

    public ProductVariantResponse toResponse(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getId(),
            variant.getSku(),
            variant.getSize(),
            variant.getColor(),
            variant.getPrice(),
                variant.getProduct() == null ? null : variant.getProduct().getId(),
                variant.getStock());
    }
}
