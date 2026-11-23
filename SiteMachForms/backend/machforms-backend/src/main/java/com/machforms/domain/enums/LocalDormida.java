package com.machforms.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LocalDormida implements RotuloEnum {

    BARRACA("Barraca"),
    CHALE("Chalé");

    private final String rotulo;

    LocalDormida(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    @JsonValue
    public String getRotulo() {
        return rotulo;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static LocalDormida deRotulo(String texto) {
        return Rotulos.resolver(LocalDormida.class, texto);
    }
}
