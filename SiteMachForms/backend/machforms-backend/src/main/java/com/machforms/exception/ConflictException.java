package com.machforms.exception;

/** Operação em conflito com dados já existentes (HTTP 409). */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
