package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.CreateMessageRequest;
import com.example.backend.dto.MessageResponse;
import com.example.backend.entity.Message;
import com.example.backend.mapper.MessageMapper;
import com.example.backend.repository.ConversationRepository;
import com.example.backend.repository.MessageRepository;

@Service
public class MessageService {
    private final MessageRepository repository;
    private final MessageMapper mapper;
    private final ConversationRepository conversationRepository;

    public MessageService(MessageRepository repository, MessageMapper mapper, ConversationRepository conversationRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.conversationRepository = conversationRepository;
    }

    public List<MessageResponse> getMessages(Long merchantId) {
        return repository.findAllByConversation_Merchant_Id(merchantId).stream().map(mapper::toResponse).toList();
    }

    public MessageResponse createMessage(CreateMessageRequest request, Long merchantId) {
        Message message = mapper.toEntity(request);
        message.setConversation(conversationRepository.findByIdAndMerchant_Id(request.conversationId(), merchantId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "No se encontró la conversación para el comercio indicado")));
        return mapper.toResponse(repository.save(message));
    }
}
