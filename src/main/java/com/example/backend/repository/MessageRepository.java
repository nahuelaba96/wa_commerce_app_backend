package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
	List<Message> findAllByConversation_Merchant_Id(Long merchantId);
}
