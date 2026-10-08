package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.CreateUserRequest;
import com.example.backend.dto.UserResponse;
import com.example.backend.entity.User;
import com.example.backend.mapper.UserMapper;
import com.example.backend.entity.MerchantUser;
import com.example.backend.repository.MerchantUserRepository;
import com.example.backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final MerchantUserRepository merchantUserRepository;

    public UserService(UserRepository repository, UserMapper mapper, MerchantUserRepository merchantUserRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.merchantUserRepository = merchantUserRepository;
    }

    public List<UserResponse> getUsers(Long merchantId) {
        return merchantUserRepository.findAllByMerchant_Id(merchantId).stream()
                .map(MerchantUser::getUser)
                .map(mapper::toResponse)
                .toList();
    }

    public UserResponse createUser(CreateUserRequest request) {
        User user = mapper.toEntity(request);
        user.setEmail(request.email());
        return mapper.toResponse(repository.save(user));
    }
}
