package com.github.dimitryivaniuta.gateway.crudms.web.mapper;

import com.github.dimitryivaniuta.gateway.crudms.domain.user.UserEntity;
import com.github.dimitryivaniuta.gateway.crudms.web.dto.user.UserResponse;

/**
 * Intentionally simple mapping (no MapStruct) to keep the sample minimal and explicit.
 */
public final class SimpleMappers {

    private SimpleMappers() {
    }

    public static UserResponse toUserResponse(UserEntity e) {
        return new UserResponse(
                e.getId(),
                e.getEmail(),
                e.getFullName(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
