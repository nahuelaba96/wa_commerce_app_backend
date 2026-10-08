package com.example.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.backend.dto.CreateConversationRequest;
import com.example.backend.dto.ConversationResponse;
import com.example.backend.service.ConversationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService service;

    public ConversationController(ConversationService service) {
        this.service = service;
    }

    @GetMapping
    public List<ConversationResponse> getConversations(@RequestParam Long merchantId) {
        return service.getConversations(merchantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse createConversation(@Valid @RequestBody CreateConversationRequest request) {
        return service.createConversation(request);
    }
}
