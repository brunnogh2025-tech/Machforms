package com.machforms.service;

import com.machforms.repository.ParticipanteRepository;
import com.machforms.service.impl.GeradorDeProtocoloAleatorio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GeradorDeProtocoloAleatorioTest {

    private final ParticipanteRepository repository = mock(ParticipanteRepository.class);
    private final GeradorDeProtocoloAleatorio gerador = new GeradorDeProtocoloAleatorio(repository);

    @Test
    void deveGerarProtocoloNoFormatoMfMaisSeisDigitos() {
        when(repository.existsByProtocolo(anyString())).thenReturn(false);

        assertTrue(gerador.proximo().matches("MF\\d{6}"));
    }

    @Test
    void deveSortearDeNovoQuandoOProtocoloJaExiste() {
        when(repository.existsByProtocolo(anyString())).thenReturn(true, true, false);

        assertTrue(gerador.proximo().matches("MF\\d{6}"));
        verify(repository, times(3)).existsByProtocolo(anyString());
    }

    @Test
    void deveFalharQuandoNaoEncontraProtocoloLivre() {
        when(repository.existsByProtocolo(anyString())).thenReturn(true);

        assertThrows(IllegalStateException.class, gerador::proximo);
    }
}
