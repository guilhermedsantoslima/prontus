package br.com.fiap.prontus.auth.dto;

import br.com.fiap.prontus.auth.model.Role;
import jakarta.validation.constraints.NotBlank;

public class AuthDto {
    public record RegisterRequest(
            @NotBlank String username,
            @NotBlank String password,
            Role role
    ) {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record AuthResponse(String token, String type, String role) {}
}

