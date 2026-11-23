package com.machforms.validation;

import com.machforms.domain.enums.LocalDormida;
import com.machforms.dto.request.CadastroParticipanteRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChaleExigeResponsavelValidator
        implements ConstraintValidator<ChaleExigeResponsavel, CadastroParticipanteRequest> {

    private static final int TAMANHO_MINIMO_NOME = 3;

    @Override
    public boolean isValid(CadastroParticipanteRequest request, ConstraintValidatorContext context) {
        if (request == null || request.dormida() != LocalDormida.CHALE) {
            return true;
        }
        boolean informado = request.responsavelChale() != null
                && request.responsavelChale().trim().length() >= TAMANHO_MINIMO_NOME;
        if (!informado) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("responsavelChale")
                    .addConstraintViolation();
        }
        return informado;
    }
}
