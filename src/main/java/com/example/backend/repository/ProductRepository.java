package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findAllByMerchant_Id(Long merchantId);
    Optional<Product> findByIdAndMerchant_Id(Long id, Long merchantId);
}
