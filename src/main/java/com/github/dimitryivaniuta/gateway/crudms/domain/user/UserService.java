package com.github.dimitryivaniuta.gateway.crudms.domain.user;

import com.github.dimitryivaniuta.gateway.crudms.web.dto.user.UserCreateRequest;
import com.github.dimitryivaniuta.gateway.crudms.web.dto.user.UserResponse;
import com.github.dimitryivaniuta.gateway.crudms.web.dto.user.UserUpdateRequest;

import java.util.List;

public interface UserService {
    UserResponse create(UserCreateRequest req);

    UserResponse update(long id, UserUpdateRequest req);

    void delete(long id);

    UserResponse get(long id);

    List<UserResponse> list();
}
