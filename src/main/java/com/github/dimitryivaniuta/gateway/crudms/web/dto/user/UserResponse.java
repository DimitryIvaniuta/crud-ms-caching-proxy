package com.github.dimitryivaniuta.gateway.crudms.web.dto.user;

import java.time.Instant;

/**
 * API response model for User.
 */
public record UserResponse(
        long id,
        String email,
        String fullName,
        Instant createdAt,
        Instant updatedAt
) {
}
