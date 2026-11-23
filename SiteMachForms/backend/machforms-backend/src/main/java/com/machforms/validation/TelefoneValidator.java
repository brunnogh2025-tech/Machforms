package com.machforms.validation;

import com.machforms.util.Normalizadores;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<Telefone, String> {

    private static final int MIN_DIGITOS = 10;
    private static final int MAX_DIGITOS = 13;

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null) {
            return true; // a obrigatoriedade é responsabilidade de @NotBlank
        }
        int digitos = Normalizadores.telefone(valor).length();
        return digitos >= MIN_DIGITOS && digitos <= MAX_DIGITOS;
    }
}
