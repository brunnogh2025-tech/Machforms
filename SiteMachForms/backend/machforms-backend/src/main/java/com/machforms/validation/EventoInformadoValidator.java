package com.machforms.validation;

import com.machforms.dto.request.CadastroParticipanteRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventoInformadoValidator implements ConstraintValidator<EventoInformado, CadastroParticipanteRequest> {

    @Override
    public boolean isValid(CadastroParticipanteRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }
        boolean informado = request.eventoId() != null
                || (request.evento() != null && !request.evento().isBlank());
        if (!informado) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("evento")
                    .addConstraintViolation();
        }
        return informado;
    }
}
