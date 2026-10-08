package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.MerchantUser;

public interface MerchantUserRepository extends JpaRepository<MerchantUser, Long> {
    List<MerchantUser> findAllByUser_Id(Long userId);
    List<MerchantUser> findAllByUser_AuthUserId(String authUserId);
    List<MerchantUser> findAllByMerchant_Id(Long merchantId);
    boolean existsByMerchant_IdAndUser_Id(Long merchantId, Long userId);
}
