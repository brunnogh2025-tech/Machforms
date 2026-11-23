package com.machforms.exception;

import com.machforms.dto.response.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduz exceções em respostas HTTP padronizadas ({@link ApiError}).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(erro -> campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors()
                .forEach(erro -> campos.putIfAbsent(erro.getObjectName(), erro.getDefaultMessage()));
        // O frontend só exibe "message"; por isso ela já traz o motivo de cada campo recusado.
        String mensagem = campos.isEmpty()
                ? "Dados inválidos. Verifique os campos informados."
                : String.join("; ", campos.values());
        return resposta(HttpStatus.BAD_REQUEST, mensagem, request, campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> tratarCorpoIlegivel(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String mensagem = "Corpo da requisição inválido. Confira o JSON, os valores permitidos "
                + "(ex.: \"Dia 1\", \"Chalé\"), as datas de nascimento em dd/mm/aaaa "
                + "e a data de início do evento (aaaa-mm-ddThh:mm).";
        return resposta(HttpStatus.BAD_REQUEST, mensagem, request, Map.of());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> tratarNaoEncontrado(ResourceNotFoundException ex, HttpServletRequest request) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage(), request, Map.of());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> tratarConflito(ConflictException ex, HttpServletRequest request) {
        return resposta(HttpStatus.CONFLICT, ex.getMessage(), request, Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> tratarIntegridade(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Violação de integridade: {}", ex.getMostSpecificCause().getMessage());
        return resposta(HttpStatus.CONFLICT, "Já existe um registro com os dados informados", request, Map.of());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> tratarAutenticacao(AuthenticationException ex, HttpServletRequest request) {
        return resposta(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos", request, Map.of());
    }

    /**
     * Rede de segurança. Erros que o próprio Spring MVC já classifica (rota inexistente, método não
     * permitido etc.) mantêm o status original; o restante vira 500 sem vazar detalhes internos.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> tratarGenerico(Exception ex, HttpServletRequest request) {
        if (ex instanceof ErrorResponse erroSpring) {
            HttpStatus status = HttpStatus.valueOf(erroSpring.getStatusCode().value());
            return resposta(status, status.getReasonPhrase(), request, Map.of());
        }
        log.error("Erro inesperado em {}", request.getRequestURI(), ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno. Tente novamente mais tarde.", request, Map.of());
    }

    private ResponseEntity<ApiError> resposta(HttpStatus status,
                                              String mensagem,
                                              HttpServletRequest request,
                                              Map<String, String> campos) {
        return ResponseEntity.status(status).body(ApiError.of(status, mensagem, request.getRequestURI(), campos));
    }
}
