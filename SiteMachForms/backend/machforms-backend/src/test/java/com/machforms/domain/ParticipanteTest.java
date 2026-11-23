package com.machforms.domain;

import com.machforms.domain.enums.DiaParticipacao;
import com.machforms.domain.enums.LocalDormida;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParticipanteTest {

    private Participante.Builder base() {
        return Participante.builder()
                .protocolo("MF123456")
                .nome("Ana Beatriz Souza")
                .email("ana@email.com")
                .telefone("11912345678")
                .dataNascimento(LocalDate.of(2005, 5, 20))
                .dias(Set.of(DiaParticipacao.DIA_1));
    }

    @Test
    void chaleMantemOResponsavel() {
        Participante participante = base().dormida(LocalDormida.CHALE).responsavelChale("Carlos Souza").build();

        assertEquals("Carlos Souza", participante.getResponsavelChale());
    }

    @Test
    void barracaDescartaOResponsavel() {
        Participante participante = base().dormida(LocalDormida.BARRACA).responsavelChale("Carlos Souza").build();

        assertNull(participante.getResponsavelChale());
    }

    @Test
    void restricoesNulasViramConjuntoVazio() {
        Participante participante = base().dormida(LocalDormida.BARRACA).restricoes(null).build();

        assertTrue(participante.getRestricoes().isEmpty());
    }
}
