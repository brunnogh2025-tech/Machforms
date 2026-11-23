package com.machforms.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DiaParticipacao implements RotuloEnum {

    DIA_1("Dia 1"),
    DIA_2("Dia 2"),
    DIA_3("Dia 3");

    private final String rotulo;

    DiaParticipacao(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    @JsonValue
    public String getRotulo() {
        return rotulo;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DiaParticipacao deRotulo(String texto) {
        return Rotulos.resolver(DiaParticipacao.class, texto);
    }
}
