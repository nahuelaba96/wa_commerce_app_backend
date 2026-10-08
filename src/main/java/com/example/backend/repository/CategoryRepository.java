package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findAllByMerchant_Id(Long merchantId);
}
