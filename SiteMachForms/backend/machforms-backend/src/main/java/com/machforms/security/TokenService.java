package com.machforms.security;

import java.util.Optional;

/**
 * Contrato para emitir e validar tokens de acesso. Quem usa não precisa saber que a implementação é JWT.
 */
public interface TokenService {

    String gerar(String emailOrganizador);

    /** @return o e-mail do organizador dono do token, ou vazio se o token for inválido, adulterado ou expirado */
    Optional<String> extrairEmail(String token);

    long validadeEmSegundos();
}
