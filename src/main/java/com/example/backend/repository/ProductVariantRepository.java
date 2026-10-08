package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
	List<ProductVariant> findAllByProduct_Merchant_Id(Long merchantId);
	Optional<ProductVariant> findByIdAndProduct_Merchant_Id(Long id, Long merchantId);
}
