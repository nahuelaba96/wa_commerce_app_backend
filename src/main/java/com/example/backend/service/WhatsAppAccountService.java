package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.CreateWhatsAppAccountRequest;
import com.example.backend.dto.WhatsAppAccountResponse;
import com.example.backend.entity.WhatsAppAccount;
import com.example.backend.mapper.WhatsAppAccountMapper;
import com.example.backend.repository.MerchantRepository;
import com.example.backend.repository.WhatsAppAccountRepository;

@Service
public class WhatsAppAccountService {
    private final WhatsAppAccountRepository repository;
    private final WhatsAppAccountMapper mapper;
    private final MerchantRepository merchantRepository;

    public WhatsAppAccountService(WhatsAppAccountRepository repository, WhatsAppAccountMapper mapper,
            MerchantRepository merchantRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.merchantRepository = merchantRepository;
    }

    public List<WhatsAppAccountResponse> getWhatsAppAccounts(Long merchantId) {
        return repository.findAllByMerchant_Id(merchantId).stream().map(mapper::toResponse).toList();
    }

    public WhatsAppAccountResponse createWhatsAppAccount(CreateWhatsAppAccountRequest request) {
        WhatsAppAccount account = mapper.toEntity(request);
        account.setMerchant(merchantRepository.findById(request.merchantId())
                .orElseThrow(() -> new RuntimeException("No se encontró el comercio con ID: " + request.merchantId())));
        return mapper.toResponse(repository.save(account));
    }
}
