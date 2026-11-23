package com.machforms.dto.response;

/**
 * Resposta do login. O frontend lê "token" e "evento" (usa evento.nomeEvento no painel);
 * "evento" é nulo se o organizador ainda não tiver evento.
 */
public record LoginResponse(
        String token,
        String tipo,
        long expiraEmSegundos,
        String nomeCriador,
        String email,
        EventoResponse evento
) {
}
