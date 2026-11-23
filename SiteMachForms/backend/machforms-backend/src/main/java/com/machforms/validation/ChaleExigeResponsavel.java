package com.machforms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Regra da tela "Continuação": quem escolhe o chalé precisa informar o responsável.
 */
@Documented
@Constraint(validatedBy = ChaleExigeResponsavelValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChaleExigeResponsavel {

    String message() default "Informe o nome do responsável pelo chalé";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
