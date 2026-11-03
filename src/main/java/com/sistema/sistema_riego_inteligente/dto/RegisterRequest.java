package com.sistema.sistema_riego_inteligente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 4, max = 50) String username,
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Debe ser un correo @gmail.com")
        String email,
        @NotBlank @Size(min = 8, max = 100) String password
) {}