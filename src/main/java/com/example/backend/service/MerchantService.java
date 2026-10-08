package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.AuthOnboardingRequest;
import com.example.backend.dto.AuthOnboardingResponse;
import com.example.backend.dto.CreateMerchantRequest;
import com.example.backend.dto.MerchantResponse;
import com.example.backend.entity.Merchant;
import com.example.backend.entity.MerchantUser;
import com.example.backend.entity.User;
import com.example.backend.mapper.MerchantMapper;
import com.example.backend.repository.MerchantRepository;
import com.example.backend.repository.MerchantUserRepository;
import com.example.backend.repository.UserRepository;
import java.util.UUID;

@Service 
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;
    private final MerchantUserRepository merchantUserRepository;
    private final UserRepository userRepository;

    public MerchantService(MerchantRepository merchantRepository, MerchantMapper merchantMapper,
            MerchantUserRepository merchantUserRepository, UserRepository userRepository) {
        this.merchantRepository = merchantRepository;
        this.merchantMapper = merchantMapper;
        this.merchantUserRepository = merchantUserRepository;
        this.userRepository = userRepository;
    }
    
    public List<MerchantResponse> getMerchantsByAuthUserID(UUID authUserID) {
        return merchantUserRepository.findAllByUser_AuthUserId(authUserID.toString()).stream()
                .map(MerchantUser::getMerchant)
                .map(merchantMapper::toResponse)
                .toList();
    }

    public MerchantResponse getMerchant(Long merchantId) {
        return merchantRepository.findById(merchantId)
            .map(merchantMapper::toResponse)
            .orElseThrow(() -> new RuntimeException("No se encontró el comercio con ID: " + merchantId));
    }

    @Transactional
    public MerchantResponse createMerchant(CreateMerchantRequest request) {
        Merchant merchant = merchantMapper.toEntity(request);
        merchant = merchantRepository.save(merchant);

        User owner = userRepository.findById(request.ownerUserId())
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + request.ownerUserId()));
        MerchantUser membership = new MerchantUser();
        membership.setMerchant(merchant);
        membership.setUser(owner);
        membership.setRole("OWNER");
        merchantUserRepository.save(membership);

        return merchantMapper.toResponse(merchant);
    }

    @Transactional
    public AuthOnboardingResponse createAuthOwner(AuthOnboardingRequest request) {
        String authUserId = request.authUserId().toString();
        User owner = userRepository.findByAuthUserId(authUserId).orElse(null);
        if (owner != null) {
            List<MerchantUser> memberships = merchantUserRepository.findAllByUser_Id(owner.getId());
            if (memberships.isEmpty()) {
                throw new IllegalStateException("Existing auth identity has no merchant membership");
            }
            return new AuthOnboardingResponse(owner.getId(), memberships.getFirst().getMerchant().getId());
        }

        owner = new User();
        owner.setAuthUserId(authUserId);
        owner.setEmail(request.email().trim().toLowerCase());
        owner.setPersonalName(request.personalName().trim());
        owner = userRepository.save(owner);

        Merchant merchant = new Merchant();
        merchant.setName(request.merchantName().trim());
        merchant = merchantRepository.save(merchant);

        MerchantUser membership = new MerchantUser();
        membership.setMerchant(merchant);
        membership.setUser(owner);
        membership.setRole("OWNER");
        merchantUserRepository.save(membership);
        return new AuthOnboardingResponse(owner.getId(), merchant.getId());
    }

}
