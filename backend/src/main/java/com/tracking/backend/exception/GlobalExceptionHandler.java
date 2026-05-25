package com.tracking.backend.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.CompletionException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String details = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .findFirst()
                .orElse("Dados invalidos.");

        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Requisicao invalida.", details);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Requisicao invalida.", ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "DATA_INTEGRITY_ERROR", "Conflito ao salvar os dados.", rootMessage(ex));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiErrorResponse> handleExternalApi(
            WebClientResponseException ex,
            HttpServletRequest request) {
        String details = "Status externo: " + ex.getStatusCode().value();
        return build(HttpStatus.BAD_GATEWAY, "EXTERNAL_API_ERROR", "Erro ao consultar API externa.", details);
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<ApiErrorResponse> handleCompletion(CompletionException ex, HttpServletRequest request) {
        return handleThrowable(unwrap(ex), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        Throwable root = unwrap(ex);
        if (root instanceof WebClientResponseException webClientException) {
            return handleExternalApi(webClientException, request);
        }

        log.error("Erro de estado inesperado em {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex.getMessage(), rootMessage(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        return handleThrowable(ex, request);
    }

    private ResponseEntity<ApiErrorResponse> handleThrowable(Throwable ex, HttpServletRequest request) {
        Throwable root = unwrap(ex);
        if (root instanceof WebClientResponseException webClientException) {
            return handleExternalApi(webClientException, request);
        }

        log.error("Erro inesperado em {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Erro interno do servidor.", rootMessage(ex));
    }

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String code,
            String message,
            String details) {
        return ResponseEntity
                .status(status)
                .body(new ApiErrorResponse(new ApiError(code, message, details)));
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private Throwable unwrap(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null &&
                (current instanceof CompletionException || current instanceof IllegalStateException)) {
            current = current.getCause();
        }
        return current;
    }

    private String rootMessage(Throwable throwable) {
        Throwable root = unwrap(throwable);
        return root.getMessage();
    }
}
