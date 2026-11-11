package com.sistema.sistema_riego_inteligente.dto;

import java.util.Map;

public record ConfiguracionResponse(Map<String, Boolean> sensores) {}