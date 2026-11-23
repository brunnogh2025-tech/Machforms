package com.machforms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Telefone com 10 a 13 dígitos (DDD + número, com ou sem código do país), aceitando máscara.
 */
@Documented
@Constraint(validatedBy = TelefoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface Telefone {

    String message() default "Informe um telefone válido com DDD";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
