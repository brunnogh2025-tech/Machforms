package com.machforms.service.impl;

import com.machforms.domain.Organizador;
import com.machforms.dto.request.LoginRequest;
import com.machforms.dto.response.EventoResponse;
import com.machforms.dto.response.LoginResponse;
import com.machforms.exception.ResourceNotFoundException;
import com.machforms.mapper.EventoMapper;
import com.machforms.repository.EventoRepository;
import com.machforms.repository.OrganizadorRepository;
import com.machforms.security.TokenService;
import com.machforms.service.AuthService;
import com.machforms.util.Normalizadores;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String TIPO_TOKEN = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final OrganizadorRepository organizadorRepository;
    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;
    private final TokenService tokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           OrganizadorRepository organizadorRepository,
                           EventoRepository eventoRepository,
                           EventoMapper eventoMapper,
                           TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.organizadorRepository = organizadorRepository;
        this.eventoRepository = eventoRepository;
        this.eventoMapper = eventoMapper;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse autenticar(LoginRequest request) {
        String email = Normalizadores.email(request.email());
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(email, request.senha()));

        Organizador organizador = organizadorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador não encontrado"));

        return new LoginResponse(
                tokenService.gerar(organizador.getEmail()),
                TIPO_TOKEN,
                tokenService.validadeEmSegundos(),
                organizador.getNome(),
                organizador.getEmail(),
                buscarEvento(organizador));
    }

    private EventoResponse buscarEvento(Organizador organizador) {
        return eventoRepository.findFirstByOrganizadorIdOrderByCriadoEmAsc(organizador.getId())
                .map(eventoMapper::toResponse)
                .orElse(null);
    }
}
