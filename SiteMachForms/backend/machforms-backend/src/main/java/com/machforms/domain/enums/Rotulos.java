package com.machforms.domain.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Converte o texto enviado pelo frontend no enum correspondente.
 * Centraliza a regra para que todos os enums se comportem da mesma forma (DRY).
 */
public final class Rotulos {

    private Rotulos() {
    }

    /**
     * @return o enum cujo rótulo (ou nome) corresponde ao texto, ou {@code null} se o texto estiver vazio
     * @throws IllegalArgumentException se o texto não corresponder a nenhum valor
     */
    public static <E extends Enum<E> & RotuloEnum> E resolver(Class<E> tipo, String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String procurado = texto.trim();
        for (E constante : tipo.getEnumConstants()) {
            if (constante.getRotulo().equalsIgnoreCase(procurado) || constante.name().equalsIgnoreCase(procurado)) {
                return constante;
            }
        }
        throw new IllegalArgumentException(
                "Valor inválido '" + texto + "'. Valores aceitos: " + valoresAceitos(tipo));
    }

    private static <E extends Enum<E> & RotuloEnum> String valoresAceitos(Class<E> tipo) {
        return Arrays.stream(tipo.getEnumConstants())
                .map(RotuloEnum::getRotulo)
                .collect(Collectors.joining(", "));
    }
}
