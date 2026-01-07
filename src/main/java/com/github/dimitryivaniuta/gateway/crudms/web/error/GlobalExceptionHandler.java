package com.github.dimitryivaniuta.gateway.crudms.web.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centralized exception handling.
 * <p>
 * Notes:
 * - Works with spring-boot-starter-web (Servlet stack).
 * - Returns consistent JSON error payloads.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            // first error per field is usually enough
            fields.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }

        return build(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                req,
                fields
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON request",
                req,
                null
        );
    }

    /**
     * Covers ResponseStatusException thrown by services.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return build(status, message, req, null);
    }

    /**
     * Covers exceptions like org.springframework.web.server.ResponseStatusException or similar,
     * and also newer Spring's ErrorResponseException.
     */
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiError> handleErrorResponseException(ErrorResponseException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        String message = ex.getBody() != null && ex.getBody().getDetail() != null
                ? ex.getBody().getDetail()
                : status.getReasonPhrase();

        return build(status, message, req, null);
    }

    /**
     * NotFoundException already has @ResponseStatus, but we return unified payload.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    /**
     * Fallback (log in real projects).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req, null);
    }

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            HttpServletRequest req,
            Map<String, String> fieldErrors
    ) {
        String requestId = req.getHeader("X-Request-Id"); // optional, if you send it
        ApiError body = ApiError.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                requestId,
                fieldErrors
        );
        return ResponseEntity.status(status).body(body);
    }
}
