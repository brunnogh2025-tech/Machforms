package com.machforms.service;

import com.machforms.domain.Organizador;

public interface OrganizadorService {

    /**
     * Cadastra um organizador com a senha criptografada.
     *
     * @throws com.machforms.exception.ConflictException se o e-mail já estiver em uso
     */
    Organizador cadastrar(DadosOrganizador dados);
}
