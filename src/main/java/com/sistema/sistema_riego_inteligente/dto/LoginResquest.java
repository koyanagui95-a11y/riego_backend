package com.sistema.sistema_riego_inteligente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginResquest(
        @NotBlank @Size(min = 4, max = 50) String username,
        @NotBlank @Size(min = 8, max = 100) String password
) {}