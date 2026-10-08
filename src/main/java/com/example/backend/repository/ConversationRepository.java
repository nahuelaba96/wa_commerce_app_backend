package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
	List<Conversation> findAllByMerchant_Id(Long merchantId);
	Optional<Conversation> findByIdAndMerchant_Id(Long id, Long merchantId);
}
