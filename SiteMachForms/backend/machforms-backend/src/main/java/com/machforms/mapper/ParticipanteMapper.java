package com.machforms.mapper;

import com.machforms.domain.Evento;
import com.machforms.domain.Participante;
import com.machforms.dto.request.CadastroParticipanteRequest;
import com.machforms.dto.response.ParticipanteResponse;
import com.machforms.util.Normalizadores;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class ParticipanteMapper {

    public Participante toEntity(CadastroParticipanteRequest request, Evento evento, String protocolo) {
        return Participante.builder()
                .protocolo(protocolo)
                .evento(evento)
                .nome(Normalizadores.texto(request.nome()))
                .email(Normalizadores.email(request.email()))
                .telefone(Normalizadores.telefone(request.telefone()))
                .dataNascimento(request.dataNasc())
                .dormida(request.dormida())
                .responsavelChale(Normalizadores.texto(request.responsavelChale()))
                .dias(request.dias())
                .restricoes(request.restricoes())
                .build();
    }

    public ParticipanteResponse toResponse(Participante participante) {
        return new ParticipanteResponse(
                participante.getProtocolo(),
                participante.getEvento().getNome(),
                participante.getNome(),
                participante.getEmail(),
                participante.getTelefone(),
                ordenados(participante.getDias()),
                participante.getDormida(),
                participante.getResponsavelChale(),
                participante.getDataNascimento(),
                ordenados(participante.getRestricoes()),
                participante.getCriadoEm());
    }

    /** Devolve na ordem de declaração do enum (Dia 1, Dia 2, Dia 3...), independente da ordem do banco. */
    private <E extends Enum<E>> List<E> ordenados(Collection<E> valores) {
        return valores.stream().sorted().toList();
    }
}
