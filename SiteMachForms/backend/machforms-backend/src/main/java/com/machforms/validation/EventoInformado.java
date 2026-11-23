package com.machforms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A inscrição precisa indicar o evento, pelo id (eventoId) ou pelo nome (evento).
 */
@Documented
@Constraint(validatedBy = EventoInformadoValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventoInformado {

    String message() default "Informe o evento em que deseja se inscrever";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
