package com.machforms.service.impl;

import com.machforms.domain.Evento;
import com.machforms.domain.Participante;
import com.machforms.dto.request.CadastroParticipanteRequest;
import com.machforms.dto.response.ParticipanteResponse;
import com.machforms.exception.ConflictException;
import com.machforms.exception.ResourceNotFoundException;
import com.machforms.mapper.ParticipanteMapper;
import com.machforms.repository.EventoRepository;
import com.machforms.repository.ParticipanteRepository;
import com.machforms.service.GeradorDeProtocolo;
import com.machforms.service.ParticipanteService;
import com.machforms.util.Normalizadores;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParticipanteServiceImpl implements ParticipanteService {

    private final ParticipanteRepository participanteRepository;
    private final EventoRepository eventoRepository;
    private final GeradorDeProtocolo geradorDeProtocolo;
    private final ParticipanteMapper participanteMapper;

    public ParticipanteServiceImpl(ParticipanteRepository participanteRepository,
                                   EventoRepository eventoRepository,
                                   GeradorDeProtocolo geradorDeProtocolo,
                                   ParticipanteMapper participanteMapper) {
        this.participanteRepository = participanteRepository;
        this.eventoRepository = eventoRepository;
        this.geradorDeProtocolo = geradorDeProtocolo;
        this.participanteMapper = participanteMapper;
    }

    @Override
    @Transactional
    public ParticipanteResponse cadastrar(CadastroParticipanteRequest request) {
        Evento evento = buscarEvento(request);
        garantirInscricaoUnica(evento, Normalizadores.email(request.email()));

        Participante participante = participanteMapper.toEntity(request, evento, geradorDeProtocolo.proximo());
        return participanteMapper.toResponse(participanteRepository.save(participante));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipanteResponse> listarDoOrganizador(String emailOrganizador) {
        String email = Normalizadores.email(emailOrganizador);
        return participanteRepository.findByEventoOrganizadorEmailOrderByCriadoEmDesc(email).stream()
                .map(participanteMapper::toResponse)
                .toList();
    }

    private Evento buscarEvento(CadastroParticipanteRequest request) {
        if (request.eventoId() != null) {
            return eventoRepository.findById(request.eventoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));
        }
        String nome = Normalizadores.texto(request.evento());
        return eventoRepository.findByNomeIgnoreCase(nome)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado: " + nome));
    }

    private void garantirInscricaoUnica(Evento evento, String email) {
        if (participanteRepository.existsByEventoIdAndEmail(evento.getId(), email)) {
            throw new ConflictException("Este e-mail já está inscrito neste evento");
        }
    }
}
