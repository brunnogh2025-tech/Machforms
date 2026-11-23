package com.machforms.controller;

import com.machforms.dto.request.CadastroEventoRequest;
import com.machforms.dto.response.EventoResponse;
import com.machforms.service.EventoService;
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
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    /** Tela "Cadastro do evento" (pública): cria o organizador e o evento. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoResponse cadastrar(@Valid @RequestBody CadastroEventoRequest request) {
        return eventoService.cadastrar(request);
    }

    /** Eventos disponíveis para inscrição (público). */
    @GetMapping
    public List<EventoResponse> listar() {
        return eventoService.listarTodos();
    }

    /** Eventos do organizador logado. */
    @GetMapping("/meus")
    public List<EventoResponse> listarMeus(Authentication autenticacao) {
        return eventoService.listarDoOrganizador(autenticacao.getName());
    }
}
