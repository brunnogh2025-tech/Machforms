package com.machforms.dto.response;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Evento como o frontend o lê: id, nomeEvento, imagem, dataInicio, localizacao, latitude, longitude e criadoEm.
 */
public record EventoResponse(
        Long id,
        String nomeEvento,
        String imagem,
        LocalDateTime dataInicio,
        String localizacao,
        Double latitude,
        Double longitude,
        Instant criadoEm
) {
}
