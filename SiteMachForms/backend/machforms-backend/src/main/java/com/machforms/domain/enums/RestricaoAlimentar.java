package com.machforms.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RestricaoAlimentar implements RotuloEnum {

    VEGETARIANO("Vegetariano"),
    ALERGICO_AMENDOIM("Alérgico a amendoim"),
    VEGANO("Vegano"),
    OUTRO("Outro");

    private final String rotulo;

    RestricaoAlimentar(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    @JsonValue
    public String getRotulo() {
        return rotulo;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RestricaoAlimentar deRotulo(String texto) {
        return Rotulos.resolver(RestricaoAlimentar.class, texto);
    }
}
