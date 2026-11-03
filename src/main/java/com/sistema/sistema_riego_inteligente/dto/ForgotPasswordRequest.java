package com.sistema.sistema_riego_inteligente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ForgotPasswordRequest(
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Debe ser un correo @gmail.com")
        String email
) {}