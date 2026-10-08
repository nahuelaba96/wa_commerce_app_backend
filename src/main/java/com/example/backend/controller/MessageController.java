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

import com.example.backend.dto.CreateMessageRequest;
import com.example.backend.dto.MessageResponse;
import com.example.backend.service.MessageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @GetMapping
    public List<MessageResponse> getMessages(@RequestParam Long merchantId) {
        return service.getMessages(merchantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createMessage(@RequestParam Long merchantId, @Valid @RequestBody CreateMessageRequest request) {
        return service.createMessage(request, merchantId);
    }
}
