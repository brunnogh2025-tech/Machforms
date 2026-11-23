package com.machforms.controller;

import com.machforms.dto.request.CadastroParticipanteRequest;
import com.machforms.dto.response.ParticipanteResponse;
import com.machforms.service.ParticipanteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inscricoes")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    public ParticipanteController(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    /** Fluxo de inscrição (público). A resposta traz o protocolo para a tela "Inscrição confirmada". */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipanteResponse cadastrar(@Valid @RequestBody CadastroParticipanteRequest request) {
        return participanteService.cadastrar(request);
    }

    /** Painel "Participantes inscritos" (exige login do organizador). */
    @GetMapping
    public List<ParticipanteResponse> listar(Authentication autenticacao) {
        return participanteService.listarDoOrganizador(autenticacao.getName());
    }
}
