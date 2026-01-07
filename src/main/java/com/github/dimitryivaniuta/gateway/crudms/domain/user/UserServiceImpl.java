package com.github.dimitryivaniuta.gateway.crudms.domain.user;

import com.github.dimitryivaniuta.gateway.crudms.cacheproxy.ProxyCacheEvict;
import com.github.dimitryivaniuta.gateway.crudms.cacheproxy.ProxyCacheable;
import com.github.dimitryivaniuta.gateway.crudms.web.dto.user.UserCreateRequest;
import com.github.dimitryivaniuta.gateway.crudms.web.dto.user.UserResponse;
import com.github.dimitryivaniuta.gateway.crudms.web.dto.user.UserUpdateRequest;
import com.github.dimitryivaniuta.gateway.crudms.web.error.NotFoundException;
import com.github.dimitryivaniuta.gateway.crudms.web.mapper.SimpleMappers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * UserService implementation with:
 * - Transaction boundaries
 * - Email normalization
 * - Conflict detection on email uniqueness
 * - Custom caching proxy integration (cache reads, evict on writes)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private static final String USERS_CACHE = "users";

    private final UserRepository repo;

    @Override
    @ProxyCacheEvict(cacheNames = {USERS_CACHE}, allEntries = true)
    public UserResponse create(UserCreateRequest req) {
        String email = normalizeEmail(req.email());

        if (repo.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User email already exists");
        }

        UserEntity e = UserEntity.builder()
                .email(email)
                .fullName(req.fullName().trim())
                .build();

        return SimpleMappers.toUserResponse(repo.save(e));
    }

    @Override
    @ProxyCacheEvict(cacheNames = {USERS_CACHE}, allEntries = true)
    public UserResponse update(long id, UserUpdateRequest req) {
        UserEntity e = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        String newEmail = normalizeEmail(req.email());
        if (!newEmail.equals(e.getEmail()) && repo.existsByEmail(newEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User email already exists");
        }

        e.setEmail(newEmail);
        e.setFullName(req.fullName().trim());

        return SimpleMappers.toUserResponse(repo.save(e));
    }

    @Override
    @ProxyCacheEvict(cacheNames = {USERS_CACHE}, allEntries = true)
    public void delete(long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("User not found: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @ProxyCacheable(cacheName = USERS_CACHE, ttlMs = 30_000)
    public UserResponse get(long id) {
        return repo.findById(id)
                .map(SimpleMappers::toUserResponse)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @ProxyCacheable(cacheName = USERS_CACHE, ttlMs = 10_000)
    public List<UserResponse> list() {
        return repo.findAll().stream().map(SimpleMappers::toUserResponse).toList();
    }

    private static String normalizeEmail(String email) {
        if (email == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        String v = email.trim().toLowerCase();
        if (v.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        return v;
    }
}
