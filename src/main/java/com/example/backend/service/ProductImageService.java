package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.CreateProductImageRequest;
import com.example.backend.dto.ProductImageResponse;
import com.example.backend.entity.ProductImage;
import com.example.backend.mapper.ProductImageMapper;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.ProductImageRepository;

@Service
public class ProductImageService {
    private final ProductImageRepository repository;
    private final ProductImageMapper mapper;
    private final ProductRepository productRepository;

    public ProductImageService(ProductImageRepository repository, ProductImageMapper mapper,
            ProductRepository productRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.productRepository = productRepository;
    }

    public List<ProductImageResponse> getProductImages(Long merchantId) {
        return repository.findAllByProduct_Merchant_Id(merchantId).stream().map(mapper::toResponse).toList();
    }

    public ProductImageResponse createProductImage(CreateProductImageRequest request, Long merchantId) {
        ProductImage image = mapper.toEntity(request);
        image.setProduct(productRepository.findByIdAndMerchant_Id(request.productId(), merchantId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "No se encontró el producto para el comercio indicado")));
        return mapper.toResponse(repository.save(image));
    }
}
