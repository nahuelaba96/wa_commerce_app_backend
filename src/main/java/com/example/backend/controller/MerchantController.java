package com.example.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.CreateMerchantRequest;
import com.example.backend.dto.MerchantResponse;
import com.example.backend.service.MerchantService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }
    

    @GetMapping("/mine")
    public List<MerchantResponse> getMyMerchants(@AuthenticationPrincipal Jwt jwt) {
        return merchantService.getMerchantsByAuthUserID(java.util.UUID.fromString(jwt.getSubject()));
    }

    @GetMapping("/{merchantId}")
    public MerchantResponse getMerchant(@PathVariable Long merchantId) {
        return merchantService.getMerchant(merchantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MerchantResponse createMerchant(@Valid @RequestBody CreateMerchantRequest request) {
        return merchantService.createMerchant(request);
    }
    
    
}
