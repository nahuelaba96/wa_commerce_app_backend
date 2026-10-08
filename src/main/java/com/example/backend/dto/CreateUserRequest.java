package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
	@Email(message = "El email no es válido")
	@NotBlank(message = "El email es obligatorio") String email) {
}
