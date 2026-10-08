package com.example.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.CreateMerchantUserRequest;
import com.example.backend.dto.MerchantUserResponse;
import com.example.backend.service.MerchantUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/merchant-users")
public class MerchantUserController {

    private final MerchantUserService service;

    public MerchantUserController(MerchantUserService service) {
        this.service = service;
    }

    @GetMapping("/merchant/{merchantId}")
    public List<MerchantUserResponse> getMemberships(@PathVariable Long merchantId) {
        return service.getMemberships(merchantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MerchantUserResponse addUser(@Valid @RequestBody CreateMerchantUserRequest request) {
        return service.addUser(request);
    }
}
