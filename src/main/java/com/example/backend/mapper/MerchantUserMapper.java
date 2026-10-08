package com.example.backend.mapper;

import org.springframework.stereotype.Component;

import com.example.backend.dto.CreateMerchantUserRequest;
import com.example.backend.dto.MerchantUserResponse;
import com.example.backend.entity.MerchantUser;

@Component
public class MerchantUserMapper {

    public MerchantUser toEntity(CreateMerchantUserRequest request) {
        MerchantUser membership = new MerchantUser();
        membership.setRole(request.role());
        return membership;
    }

    public MerchantUserResponse toResponse(MerchantUser membership) {
        return new MerchantUserResponse(
                membership.getId(),
                membership.getMerchant().getId(),
                membership.getUser().getId(),
                membership.getRole());
    }
}
