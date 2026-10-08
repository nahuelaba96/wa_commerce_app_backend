package com.example.backend.mapper;

import org.springframework.stereotype.Component;

import com.example.backend.dto.CreateMessageRequest;
import com.example.backend.dto.MessageResponse;
import com.example.backend.entity.Message;

@Component
public class MessageMapper {
    public Message toEntity(CreateMessageRequest request) {
        return new Message();
    }

    public MessageResponse toResponse(Message message) {
        return new MessageResponse(message.getId(), message.getConversation() == null ? null : message.getConversation().getId());
    }
}
