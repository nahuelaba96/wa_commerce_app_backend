package com.example.backend.mapper;

import org.springframework.stereotype.Component;

import com.example.backend.dto.CreateCategoryRequest;
import com.example.backend.dto.CategoryResponse;
import com.example.backend.entity.Category;

@Component
public class CategoryMapper {
    public Category toEntity(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        return category;
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(),
            category.getMerchant() == null ? null : category.getMerchant().getId());
    }
}
