package com.example.backend.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.backend.dto.CreateProductRequest;
import com.example.backend.dto.ProductResponse;
import com.example.backend.dto.ProductVariantResponse;
import com.example.backend.dto.UpdateProductRequest;
import com.example.backend.entity.Product;

@Component
public class ProductMapper {
    public Product toEntity(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setBrand(request.brand());
        return product;
    }

    public void updateEntity(Product product, UpdateProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setBrand(request.brand());
    }

public ProductResponse toResponse(Product product) {
    CategoryMapper categoryMapper = new CategoryMapper();
    List<ProductVariantResponse> variants =
        product.getProductVariants()
            .stream()
            .map(variant -> new ProductVariantResponse(
                variant.getId(),
                variant.getSku(),
                variant.getSize(),
                variant.getColor(),
                variant.getPrice(),
                variant.getProduct().getId(),
                variant.getStock()
            ))
            .toList();

    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getBrand(),
        product.getMerchant() == null
            ? null
            : product.getMerchant().getId(),
        variants,
        categoryMapper.toResponse(product.getCategory())
    );
    }
}
