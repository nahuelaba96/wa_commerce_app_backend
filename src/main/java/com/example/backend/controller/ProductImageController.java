package com.example.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.backend.dto.CreateProductImageRequest;
import com.example.backend.dto.ProductImageResponse;
import com.example.backend.service.ProductImageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/product-images")
public class ProductImageController {
    private final ProductImageService service;

    public ProductImageController(ProductImageService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductImageResponse> getProductImages(@RequestParam Long merchantId) {
        return service.getProductImages(merchantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductImageResponse createProductImage(@RequestParam Long merchantId, @Valid @RequestBody CreateProductImageRequest request) {
        return service.createProductImage(request, merchantId);
    }
}
