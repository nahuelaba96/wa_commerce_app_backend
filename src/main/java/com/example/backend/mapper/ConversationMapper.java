package com.example.backend.mapper;

import org.springframework.stereotype.Component;

import com.example.backend.dto.CreateConversationRequest;
import com.example.backend.dto.ConversationResponse;
import com.example.backend.entity.Conversation;

@Component
public class ConversationMapper {
    public Conversation toEntity(CreateConversationRequest request) {
        return new Conversation();
    }

    public ConversationResponse toResponse(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getMerchant() == null ? null : conversation.getMerchant().getId(),
                conversation.getCustomer() == null ? null : conversation.getCustomer().getId(),
                conversation.getWhatsappAccount() == null ? null : conversation.getWhatsappAccount().getId());
    }
}
