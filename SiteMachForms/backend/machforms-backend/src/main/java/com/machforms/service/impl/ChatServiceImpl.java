package com.machforms.service.impl;

import com.machforms.service.ChatService;
import org.springframework.stereotype.Service;

/**
 * Mantém o comportamento que o frontend tinha localmente: confirmar o recebimento da mensagem.
 * Para um chat de verdade (mensagens entre organizador e participantes), basta outra implementação
 * de {@link ChatService}.
 */
@Service
public class ChatServiceImpl implements ChatService {

    private static final String RESPOSTA_PADRAO = "Recebi sua mensagem!";

    @Override
    public String responder(String mensagem) {
        return RESPOSTA_PADRAO;
    }
}
