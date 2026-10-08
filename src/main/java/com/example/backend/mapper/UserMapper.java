package com.example.backend.mapper;

import org.springframework.stereotype.Component;

import com.example.backend.dto.CreateUserRequest;
import com.example.backend.dto.UserResponse;
import com.example.backend.entity.User;

@Component
public class UserMapper {
    public User toEntity(CreateUserRequest request) {
        return new User();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail());
    }
}
