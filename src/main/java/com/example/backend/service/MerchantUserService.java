package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.CreateMerchantUserRequest;
import com.example.backend.dto.MerchantUserResponse;
import com.example.backend.entity.MerchantUser;
import com.example.backend.mapper.MerchantUserMapper;
import com.example.backend.repository.MerchantRepository;
import com.example.backend.repository.MerchantUserRepository;
import com.example.backend.repository.UserRepository;

@Service
public class MerchantUserService {

    private final MerchantUserRepository repository;
    private final MerchantUserMapper mapper;
    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;

    public MerchantUserService(MerchantUserRepository repository, MerchantUserMapper mapper,
            MerchantRepository merchantRepository, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.merchantRepository = merchantRepository;
        this.userRepository = userRepository;
    }

    public List<MerchantUserResponse> getMemberships(Long merchantId) {
        return repository.findAllByMerchant_Id(merchantId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public MerchantUserResponse addUser(CreateMerchantUserRequest request) {
        if (repository.existsByMerchant_IdAndUser_Id(request.merchantId(), request.userId())) {
            throw new IllegalArgumentException("El usuario ya pertenece a este comercio");
        }

        MerchantUser membership = mapper.toEntity(request);
        membership.setMerchant(merchantRepository.findById(request.merchantId())
                .orElseThrow(() -> new RuntimeException("No se encontró el comercio con ID: " + request.merchantId())));
        membership.setUser(userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + request.userId())));

        return mapper.toResponse(repository.save(membership));
    }
}
