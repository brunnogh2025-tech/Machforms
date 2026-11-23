package com.machforms.mapper;

import com.machforms.domain.Evento;
import com.machforms.domain.Organizador;
import com.machforms.dto.request.CadastroEventoRequest;
import com.machforms.dto.response.EventoResponse;
import com.machforms.service.DadosOrganizador;
import com.machforms.util.Normalizadores;
import org.springframework.stereotype.Component;

@Component
public class EventoMapper {

    public DadosOrganizador toDadosOrganizador(CadastroEventoRequest request) {
        return new DadosOrganizador(
                request.nomeCriador(),
                request.dataNasc(),
                request.telefone(),
                request.email(),
                request.senha());
    }

    public Evento toEntity(CadastroEventoRequest request, Organizador organizador) {
        return Evento.builder()
                .nome(Normalizadores.texto(request.nomeEvento()))
                .organizador(organizador)
                .imagem(Normalizadores.textoOuNulo(request.imagem()))
                .dataInicio(request.dataInicio())
                .localizacao(Normalizadores.texto(request.localizacao()))
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();
    }

    public EventoResponse toResponse(Evento evento) {
        return new EventoResponse(
                evento.getId(),
                evento.getNome(),
                evento.getImagem(),
                evento.getDataInicio(),
                evento.getLocalizacao(),
                evento.getLatitude(),
                evento.getLongitude(),
                evento.getCriadoEm());
    }
}
