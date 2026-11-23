package com.machforms.service;

import com.machforms.dto.request.CadastroEventoRequest;
import com.machforms.dto.response.EventoResponse;

import java.util.List;

public interface EventoService {

    /** Cadastra o organizador e o evento dele na mesma operação. */
    EventoResponse cadastrar(CadastroEventoRequest request);

    List<EventoResponse> listarTodos();

    List<EventoResponse> listarDoOrganizador(String emailOrganizador);
}
