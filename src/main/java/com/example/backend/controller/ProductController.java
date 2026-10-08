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

import com.example.backend.dto.CreateProductRequest;
import com.example.backend.dto.ProductResponse;
import com.example.backend.dto.UpdateProductRequest;
import com.example.backend.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> getProducts(@RequestParam Long merchantId) {
        return service.getProducts(merchantId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@RequestParam Long merchantId, @RequestParam Long id) {
        service.deleteProduct(merchantId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return service.createProduct(request);
    }

    @PutMapping
    public ProductResponse updateProduct(
            @RequestParam Long merchantId,
            @RequestParam Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return service.updateProduct(merchantId, id, request);
    }
}
