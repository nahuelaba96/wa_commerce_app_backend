package com.example.backend.mapper;

import org.springframework.stereotype.Component;

import com.example.backend.dto.CreateWhatsAppAccountRequest;
import com.example.backend.dto.WhatsAppAccountResponse;
import com.example.backend.entity.WhatsAppAccount;

@Component
public class WhatsAppAccountMapper {
    public WhatsAppAccount toEntity(CreateWhatsAppAccountRequest request) {
        return new WhatsAppAccount();
    }

    public WhatsAppAccountResponse toResponse(WhatsAppAccount account) {
        return new WhatsAppAccountResponse(account.getId(), account.getMerchant() == null ? null : account.getMerchant().getId());
    }
}
