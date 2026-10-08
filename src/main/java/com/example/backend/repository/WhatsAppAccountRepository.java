package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.WhatsAppAccount;

public interface WhatsAppAccountRepository extends JpaRepository<WhatsAppAccount, Long> {
	List<WhatsAppAccount> findAllByMerchant_Id(Long merchantId);
}
