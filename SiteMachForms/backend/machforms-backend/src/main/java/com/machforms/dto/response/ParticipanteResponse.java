package com.machforms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.machforms.domain.enums.DiaParticipacao;
import com.machforms.domain.enums.LocalDormida;
import com.machforms.domain.enums.RestricaoAlimentar;
import com.machforms.util.FormatoData;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Os nomes dos campos seguem o objeto "registro" montado pelo frontend
 * (protocolo, evento, nome, email, telefone, dias, dormida, responsavelChale, dataNasc, restricoes).
 */
public record ParticipanteResponse(
        String protocolo,
        String evento,
        String nome,
        String email,
        String telefone,
        List<DiaParticipacao> dias,
        LocalDormida dormida,
        String responsavelChale,
        @JsonFormat(pattern = FormatoData.PADRAO) LocalDate dataNasc,
        List<RestricaoAlimentar> restricoes,
        Instant inscritoEm
) {
}
