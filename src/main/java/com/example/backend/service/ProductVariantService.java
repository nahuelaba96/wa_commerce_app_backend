package com.example.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.dto.CreateProductVariantRequest;
import com.example.backend.dto.ProductVariantResponse;
import com.example.backend.dto.UpdateProductVariantRequest;
import com.example.backend.entity.ProductVariant;
import com.example.backend.mapper.ProductVariantMapper;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.ProductVariantRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductVariantService {
    private final ProductVariantRepository repository;
    private final ProductVariantMapper mapper;
    private final ProductRepository productRepository;

    public ProductVariantService(ProductVariantRepository repository, ProductVariantMapper mapper,
            ProductRepository productRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.productRepository = productRepository;
    }

    public List<ProductVariantResponse> getProductVariants(Long merchantId) {
        return repository.findAllByProduct_Merchant_Id(merchantId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public ProductVariantResponse updateProductVariant(
            Long merchantId,
            Long id,
            UpdateProductVariantRequest request) {
        ProductVariant variant = repository.findByIdAndProduct_Merchant_Id(id, merchantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró la variante con ID: " + id + " para el comercio indicado"));

        mapper.updateEntity(variant, request);

        return mapper.toResponse(repository.save(variant));
    }

    public void deleteProductVariant(Long merchantId, Long id) {
        ProductVariant variant = repository.findByIdAndProduct_Merchant_Id(id, merchantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró la variante con ID: " + id + " para el comercio indicado"));

        repository.delete(variant);
    }

    public ProductVariantResponse createProductVariant(CreateProductVariantRequest request) {
        ProductVariant variant = mapper.toEntity(request);
        variant.setProduct(productRepository.findByIdAndMerchant_Id(request.productId(), request.merchantId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "El producto no existe o no pertenece al comercio indicado")));
        return mapper.toResponse(repository.save(variant));
    }
}
