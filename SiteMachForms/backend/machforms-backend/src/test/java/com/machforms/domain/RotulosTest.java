package com.machforms.domain;

import com.machforms.domain.enums.DiaParticipacao;
import com.machforms.domain.enums.LocalDormida;
import com.machforms.domain.enums.RestricaoAlimentar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RotulosTest {

    @Test
    void deveConverterOsRotulosExatosDoFrontend() {
        assertEquals(DiaParticipacao.DIA_1, DiaParticipacao.deRotulo("Dia 1"));
        assertEquals(LocalDormida.CHALE, LocalDormida.deRotulo("Chalé"));
        assertEquals(LocalDormida.BARRACA, LocalDormida.deRotulo("Barraca"));
        assertEquals(RestricaoAlimentar.ALERGICO_AMENDOIM, RestricaoAlimentar.deRotulo("Alérgico a amendoim"));
        assertEquals(RestricaoAlimentar.OUTRO, RestricaoAlimentar.deRotulo("Outro"));
    }

    @Test
    void deveIgnorarEspacosEDiferencaDeMaiusculas() {
        assertEquals(DiaParticipacao.DIA_3, DiaParticipacao.deRotulo("  dia 3 "));
    }

    @Test
    void deveAceitarOPropioNomeDoEnum() {
        assertEquals(LocalDormida.CHALE, LocalDormida.deRotulo("CHALE"));
    }

    @Test
    void textoVazioViraNuloParaQueAValidacaoReclame() {
        assertNull(LocalDormida.deRotulo(""));
        assertNull(LocalDormida.deRotulo(null));
    }

    @Test
    void valorDesconhecidoDeveLancarExcecaoComOsValoresAceitos() {
        IllegalArgumentException erro =
                assertThrows(IllegalArgumentException.class, () -> DiaParticipacao.deRotulo("Dia 9"));

        assertEquals("Valor inválido 'Dia 9'. Valores aceitos: Dia 1, Dia 2, Dia 3", erro.getMessage());
    }
}
