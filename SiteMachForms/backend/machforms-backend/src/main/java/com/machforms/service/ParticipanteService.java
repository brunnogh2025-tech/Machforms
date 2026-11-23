package com.machforms.service;

import com.machforms.dto.request.CadastroParticipanteRequest;
import com.machforms.dto.response.ParticipanteResponse;

import java.util.List;

public interface ParticipanteService {

    ParticipanteResponse cadastrar(CadastroParticipanteRequest request);

    /** Inscrições de todos os eventos do organizador informado, da mais recente para a mais antiga. */
    List<ParticipanteResponse> listarDoOrganizador(String emailOrganizador);
}
