package com.example.backend.mapper;

import org.springframework.stereotype.Component;

import com.example.backend.dto.CreateMerchantRequest;
import com.example.backend.dto.MerchantResponse;
import com.example.backend.entity.Merchant;

@Component 
public class MerchantMapper {
    public MerchantResponse toResponse(Merchant merchant) {
        return new MerchantResponse(merchant.getId(), merchant.getName());
    }

    public Merchant toEntity(CreateMerchantRequest request) {
        Merchant merchant = new Merchant();
        merchant.setName(request.name());
        return merchant;
    }
}
