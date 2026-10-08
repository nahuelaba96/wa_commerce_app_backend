package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
	List<ProductImage> findAllByProduct_Merchant_Id(Long merchantId);
}
