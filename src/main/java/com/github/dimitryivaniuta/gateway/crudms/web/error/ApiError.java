package com.github.dimitryivaniuta.gateway.crudms.web.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/**
 * Standard API error payload.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String requestId,
        Map<String, String> fieldErrors
) {
    public static ApiError of(
            int status,
            String error,
            String message,
            String path,
            String requestId,
            Map<String, String> fieldErrors
    ) {
        return new ApiError(Instant.now(), status, error, message, path, requestId, fieldErrors);
    }
}
