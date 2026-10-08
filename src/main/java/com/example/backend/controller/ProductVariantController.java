package com.example.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.backend.dto.CreateProductVariantRequest;
import com.example.backend.dto.ProductVariantResponse;
import com.example.backend.dto.UpdateProductVariantRequest;
import com.example.backend.service.ProductVariantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/product-variants")
public class ProductVariantController {
    private final ProductVariantService service;

    public ProductVariantController(ProductVariantService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductVariantResponse> getProductVariants(@RequestParam Long merchantId) {
        return service.getProductVariants(merchantId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductVariant(@RequestParam Long merchantId, @RequestParam Long id) {
        service.deleteProductVariant(merchantId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductVariantResponse createProductVariant(@Valid @RequestBody CreateProductVariantRequest request) {
        return service.createProductVariant(request);
    }

    @PutMapping
    public ProductVariantResponse updateProductVariant(
            @RequestParam Long merchantId,
            @RequestParam Long id,
            @Valid @RequestBody UpdateProductVariantRequest request) {
        return service.updateProductVariant(merchantId, id, request);
    }
}
