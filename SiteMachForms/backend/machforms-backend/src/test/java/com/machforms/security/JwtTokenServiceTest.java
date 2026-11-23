package com.machforms.security;

import com.machforms.config.JwtProperties;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    private static final String SEGREDO = "segredo-de-teste-com-mais-de-32-caracteres-ok";

    private final JwtTokenService service = new JwtTokenService(new JwtProperties(SEGREDO, Duration.ofMinutes(5)));

    @Test
    void deveGerarTokenQueDevolveOMesmoEmail() {
        String token = service.gerar("ana@email.com");

        assertEquals("ana@email.com", service.extrairEmail(token).orElseThrow());
    }

    @Test
    void deveRejeitarTokenAdulterado() {
        String token = service.gerar("ana@email.com");

        assertTrue(service.extrairEmail(token + "x").isEmpty());
    }

    @Test
    void deveRejeitarTokenAssinadoComOutraChave() {
        JwtTokenService outro = new JwtTokenService(
                new JwtProperties("outro-segredo-de-teste-com-mais-de-32-caracteres", Duration.ofMinutes(5)));

        assertTrue(service.extrairEmail(outro.gerar("ana@email.com")).isEmpty());
    }

    @Test
    void deveRejeitarTokenExpirado() {
        JwtTokenService expirado = new JwtTokenService(new JwtProperties(SEGREDO, Duration.ofSeconds(-10)));

        assertTrue(service.extrairEmail(expirado.gerar("ana@email.com")).isEmpty());
    }

    @Test
    void deveRejeitarTextoQueNaoEUmToken() {
        assertTrue(service.extrairEmail("isso-nao-e-um-jwt").isEmpty());
    }

    @Test
    void deveInformarValidadeEmSegundos() {
        assertEquals(300, service.validadeEmSegundos());
    }
}
