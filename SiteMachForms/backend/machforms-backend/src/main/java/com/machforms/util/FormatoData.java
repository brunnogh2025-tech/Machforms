package com.machforms.util;

/**
 * Formato de data usado pelo frontend (dd/mm/aaaa).
 * "uuuu" (e não "yyyy") permite a leitura estrita: datas como 31/02/2000 são rejeitadas.
 */
public final class FormatoData {

    public static final String PADRAO = "dd/MM/uuuu";

    private FormatoData() {
    }
}
