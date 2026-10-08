package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.CreateConversationRequest;
import com.example.backend.dto.ConversationResponse;
import com.example.backend.entity.Conversation;
import com.example.backend.mapper.ConversationMapper;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.MerchantRepository;
import com.example.backend.repository.ConversationRepository;
import com.example.backend.repository.WhatsAppAccountRepository;

@Service
public class ConversationService {
    private final ConversationRepository repository;
    private final ConversationMapper mapper;
    private final MerchantRepository merchantRepository;
    private final CustomerRepository customerRepository;
    private final WhatsAppAccountRepository whatsappAccountRepository;

    public ConversationService(ConversationRepository repository, ConversationMapper mapper,
            MerchantRepository merchantRepository, CustomerRepository customerRepository,
            WhatsAppAccountRepository whatsappAccountRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.merchantRepository = merchantRepository;
        this.customerRepository = customerRepository;
        this.whatsappAccountRepository = whatsappAccountRepository;
    }

    public List<ConversationResponse> getConversations(Long merchantId) {
        return repository.findAllByMerchant_Id(merchantId).stream().map(mapper::toResponse).toList();
    }

        public ConversationResponse createConversation(CreateConversationRequest request) {
        Conversation conversation = mapper.toEntity(request);
        conversation.setMerchant(merchantRepository.findById(request.merchantId())
                .orElseThrow(() -> new RuntimeException("No se encontró el comercio con ID: " + request.merchantId())));

        conversation.setCustomer(customerRepository.findById(request.customerId())
            .filter(customer -> customer.getMerchant() != null
                && request.merchantId().equals(customer.getMerchant().getId()))
                .orElseThrow(() -> new RuntimeException("El cliente no pertenece al comercio indicado")));

        if (request.whatsappAccountId() != null) {
            conversation.setWhatsappAccount(whatsappAccountRepository.findById(request.whatsappAccountId())
                .filter(account -> account.getMerchant() != null
                    && request.merchantId().equals(account.getMerchant().getId()))
                    .orElseThrow(() -> new RuntimeException("La cuenta de WhatsApp no pertenece al comercio indicado")));
        }

        return mapper.toResponse(repository.save(conversation));
        }
}
