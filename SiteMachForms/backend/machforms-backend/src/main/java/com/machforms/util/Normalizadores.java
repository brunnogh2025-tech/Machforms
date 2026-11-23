package com.machforms.util;

import java.util.Locale;

/**
 * Padroniza textos antes de gravar ou comparar.
 */
public final class Normalizadores {

    private Normalizadores() {
    }

    public static String texto(String valor) {
        return valor == null ? null : valor.trim();
    }

    /** Campos opcionais: o frontend envia "" quando o usuário não preenche. */
    public static String textoOuNulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    public static String email(String valor) {
        return valor == null ? null : valor.trim().toLowerCase(Locale.ROOT);
    }

    public static String emailOuNulo(String valor) {
        String texto = textoOuNulo(valor);
        return texto == null ? null : texto.toLowerCase(Locale.ROOT);
    }

    /** Mantém apenas os dígitos: "(11) 91234-5678" vira "11912345678". */
    public static String telefone(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }
}
