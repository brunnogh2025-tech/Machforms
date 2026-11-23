package com.machforms.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

/**
 * Formato único de erro devolvido pela API.
 * "message" é o texto que o frontend exibe (lê {@code data.message}); "campos" traz a mensagem de cada
 * campo inválido, caso o frontend queira mostrá-las sob cada input.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
        Instant timestamp,
        int status,
        String erro,
        String message,
        String caminho,
        Map<String, String> campos
) {

    public static ApiError of(HttpStatus status, String message, String caminho) {
        return of(status, message, caminho, Map.of());
    }

    public static ApiError of(HttpStatus status, String message, String caminho, Map<String, String> campos) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, caminho, campos);
    }
}
