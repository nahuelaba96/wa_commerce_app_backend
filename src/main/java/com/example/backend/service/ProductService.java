package com.example.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.dto.CreateProductRequest;
import com.example.backend.dto.CreateProductVariantRequest;
import com.example.backend.dto.CreateProductVariantRequestFromProduct;
import com.example.backend.dto.ProductResponse;
import com.example.backend.dto.UpdateProductRequest;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductVariant;
import com.example.backend.mapper.ProductMapper;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.MerchantRepository;
import com.example.backend.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final MerchantRepository merchantRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository repository, ProductMapper mapper, MerchantRepository merchantRepository,
            CategoryRepository categoryRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.merchantRepository = merchantRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductResponse> getProducts(Long merchantId) {
        return repository.findAllByMerchant_Id(merchantId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public ProductResponse updateProduct(Long merchantId, Long id, UpdateProductRequest request) {
        Product product = repository.findByIdAndMerchant_Id(id, merchantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el producto con ID: " + id + " para el comercio indicado"));

        var category = categoryRepository.findById(request.categoryId())
                .filter(candidate -> candidate.getMerchant() != null
                        && merchantId.equals(candidate.getMerchant().getId()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La categoría no pertenece al comercio indicado"));

        mapper.updateEntity(product, request);
        product.setCategory(category);

        return mapper.toResponse(repository.save(product));
    }

    @Transactional
    public void deleteProduct(Long merchantId, Long id) {
        Product product = repository.findByIdAndMerchant_Id(id, merchantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el producto con ID: " + id + " para el comercio indicado"));

        repository.delete(product);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {    

    Product product = mapper.toEntity(request);

    product.setMerchant(
        merchantRepository.findById(request.merchantId())
            .orElseThrow(() ->
                new RuntimeException(
                    "No se encontró el comercio con ID: " + request.merchantId()
                )
            )
    );

    if (request.categoryId() != null) {
        product.setCategory(
            categoryRepository.findById(request.categoryId())
                .filter(category ->
                    category.getMerchant() != null &&
                    request.merchantId().equals(category.getMerchant().getId())
                )
                .orElseThrow(() ->
                    new RuntimeException(
                        "La categoría no pertenece al comercio indicado"
                    )
                )
        );
    }

    if (request.variants() != null) {
        for (CreateProductVariantRequestFromProduct variantRequest : request.variants()) {

            ProductVariant variant = new ProductVariant();

            variant.setSku(variantRequest.sku());
            variant.setSize(variantRequest.size());
            variant.setColor(variantRequest.color());
            variant.setPrice(variantRequest.price());
            variant.setStock(variantRequest.stock());

            variant.setProduct(product);

            product.getProductVariants().add(variant);
        }
    }

    return mapper.toResponse(repository.save(product));
    }
}
