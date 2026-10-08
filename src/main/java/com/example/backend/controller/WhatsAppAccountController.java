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

import com.example.backend.dto.CreateWhatsAppAccountRequest;
import com.example.backend.dto.WhatsAppAccountResponse;
import com.example.backend.service.WhatsAppAccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/whatsapp-accounts")
public class WhatsAppAccountController {
    private final WhatsAppAccountService service;

    public WhatsAppAccountController(WhatsAppAccountService service) {
        this.service = service;
    }

    @GetMapping
    public List<WhatsAppAccountResponse> getWhatsAppAccounts(@RequestParam Long merchantId) {
        return service.getWhatsAppAccounts(merchantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WhatsAppAccountResponse createWhatsAppAccount(@Valid @RequestBody CreateWhatsAppAccountRequest request) {
        return service.createWhatsAppAccount(request);
    }
}
