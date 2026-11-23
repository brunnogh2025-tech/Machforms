package com.machforms.service.impl;

import com.machforms.repository.ParticipanteRepository;
import com.machforms.service.GeradorDeProtocolo;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Protocolo no formato "MF" + 6 dígitos, mesmo padrão que o frontend já exibia.
 * Sorteia até encontrar um número ainda não utilizado.
 */
@Component
public class GeradorDeProtocoloAleatorio implements GeradorDeProtocolo {

    private static final String PREFIXO = "MF";
    private static final int LIMITE = 1_000_000;
    private static final int MAX_TENTATIVAS = 20;

    private final SecureRandom random = new SecureRandom();
    private final ParticipanteRepository participanteRepository;

    public GeradorDeProtocoloAleatorio(ParticipanteRepository participanteRepository) {
        this.participanteRepository = participanteRepository;
    }

    @Override
    public String proximo() {
        for (int tentativa = 0; tentativa < MAX_TENTATIVAS; tentativa++) {
            String candidato = sortear();
            if (!participanteRepository.existsByProtocolo(candidato)) {
                return candidato;
            }
        }
        throw new IllegalStateException("Não foi possível gerar um protocolo único");
    }

    private String sortear() {
        return String.format("%s%06d", PREFIXO, random.nextInt(LIMITE));
    }
}
