package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Merchant;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}
