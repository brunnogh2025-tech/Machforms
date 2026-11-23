package com.machforms.service;

import java.time.LocalDate;

/**
 * Dados necessários para cadastrar um organizador, independentes do formato da requisição HTTP.
 */
public record DadosOrganizador(
        String nome,
        LocalDate dataNascimento,
        String telefone,
        String email,
        String senha
) {
}
