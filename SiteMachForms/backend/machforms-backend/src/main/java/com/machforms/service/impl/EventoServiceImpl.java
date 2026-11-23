package com.machforms.service.impl;

import com.machforms.domain.Evento;
import com.machforms.domain.Organizador;
import com.machforms.dto.request.CadastroEventoRequest;
import com.machforms.dto.response.EventoResponse;
import com.machforms.exception.ConflictException;
import com.machforms.mapper.EventoMapper;
import com.machforms.repository.EventoRepository;
import com.machforms.service.EventoService;
import com.machforms.service.OrganizadorService;
import com.machforms.util.Normalizadores;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;
    private final OrganizadorService organizadorService;
    private final EventoMapper eventoMapper;

    public EventoServiceImpl(EventoRepository eventoRepository,
                             OrganizadorService organizadorService,
                             EventoMapper eventoMapper) {
        this.eventoRepository = eventoRepository;
        this.organizadorService = organizadorService;
        this.eventoMapper = eventoMapper;
    }

    @Override
    @Transactional
    public EventoResponse cadastrar(CadastroEventoRequest request) {
        String nomeEvento = Normalizadores.texto(request.nomeEvento());
        garantirNomeDisponivel(nomeEvento);

        Organizador organizador = organizadorService.cadastrar(eventoMapper.toDadosOrganizador(request));
        Evento evento = eventoRepository.save(eventoMapper.toEntity(request, organizador));
        return eventoMapper.toResponse(evento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoResponse> listarTodos() {
        return eventoRepository.findAllByOrderByNomeAsc().stream()
                .map(eventoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoResponse> listarDoOrganizador(String emailOrganizador) {
        return eventoRepository.findByOrganizadorEmailOrderByNomeAsc(Normalizadores.email(emailOrganizador)).stream()
                .map(eventoMapper::toResponse)
                .toList();
    }

    private void garantirNomeDisponivel(String nomeEvento) {
        if (eventoRepository.existsByNomeIgnoreCase(nomeEvento)) {
            throw new ConflictException("Já existe um evento com este nome");
        }
    }
}
