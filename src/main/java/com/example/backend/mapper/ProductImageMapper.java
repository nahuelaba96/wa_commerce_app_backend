package com.example.backend.mapper;

import org.springframework.stereotype.Component;

import com.example.backend.dto.CreateProductImageRequest;
import com.example.backend.dto.ProductImageResponse;
import com.example.backend.entity.ProductImage;

@Component
public class ProductImageMapper {
    public ProductImage toEntity(CreateProductImageRequest request) {
        return new ProductImage();
    }

    public ProductImageResponse toResponse(ProductImage image) {
        return new ProductImageResponse(image.getId(), image.getProduct() == null ? null : image.getProduct().getId());
    }
}
