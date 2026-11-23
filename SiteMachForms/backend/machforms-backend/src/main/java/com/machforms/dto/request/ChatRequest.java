package com.machforms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Mensagem enviada pela tela de chat: o frontend envia {@code { "message": "..." }}.
 */
public record ChatRequest(
        @NotBlank(message = "Digite uma mensagem")
        @Size(max = 1000, message = "A mensagem deve ter no máximo 1000 caracteres")
        String message
) {
}
