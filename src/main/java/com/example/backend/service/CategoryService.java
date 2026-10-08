package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.CreateCategoryRequest;
import com.example.backend.dto.CategoryResponse;
import com.example.backend.entity.Category;
import com.example.backend.mapper.CategoryMapper;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.MerchantRepository;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;
    private final MerchantRepository merchantRepository;

    public CategoryService(CategoryRepository repository, CategoryMapper mapper, MerchantRepository merchantRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.merchantRepository = merchantRepository;
    }

    public List<CategoryResponse> getCategories(Long merchantId) {
        return repository.findAllByMerchant_Id(merchantId).stream().map(mapper::toResponse).toList();
    }

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = mapper.toEntity(request);
        category.setMerchant(merchantRepository.findById(request.merchantId())
                .orElseThrow(() -> new RuntimeException("No se encontró el comercio con ID: " + request.merchantId())));
        return mapper.toResponse(repository.save(category));
    }
}
