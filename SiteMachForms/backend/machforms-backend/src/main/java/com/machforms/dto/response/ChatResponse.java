package com.machforms.dto.response;

/**
 * Resposta do chat: o frontend exibe {@code data.reply} como mensagem do bot.
 */
public record ChatResponse(String reply) {
}
