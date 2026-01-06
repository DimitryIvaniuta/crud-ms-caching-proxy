package com.github.dimitryivaniuta.gateway.crudms.domain.user;

import com.github.dimitryivaniuta.gateway.crudms.cacheproxy.ProxyCacheEvict;
import com.github.dimitryivaniuta.gateway.crudms.cacheproxy.ProxyCacheable;
import com.github.dimitryivaniuta.gateway.crudms.web.dto.user.*;
import com.github.dimitryivaniuta.gateway.crudms.web.error.NotFoundException;
import com.github.dimitryivaniuta.gateway.crudms.web.mapper.SimpleMappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    @Override
    @ProxyCacheEvict(cacheNames = {"users"}, allEntries = true) // write => invalidate cache
    public UserResponse create(UserCreateRequest req) {
        UserEntity e = new UserEntity();
        e.setEmail(req.email());
        e.setFullName(req.fullName());
        return SimpleMappers.toUserResponse(repo.save(e));
    }

    @Override
    @ProxyCacheEvict(cacheNames = {"users"}, allEntries = true)
    public UserResponse update(long id, UserUpdateRequest req) {
        UserEntity e = repo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
        e.setEmail(req.email());
        e.setFullName(req.fullName());
        return SimpleMappers.toUserResponse(repo.save(e));
    }

    @Override
    @ProxyCacheEvict(cacheNames = {"users"}, allEntries = true)
    public void delete(long id) {
        if (!repo.existsById(id)) throw new NotFoundException("User not found: " + id);
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @ProxyCacheable(cacheName = "users", ttlMs = 30_000) // read => cached
    public UserResponse get(long id) {
        return repo.findById(id)
                .map(SimpleMappers::toUserResponse)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @ProxyCacheable(cacheName = "users", ttlMs = 10_000)
    public List<UserResponse> list() {
        return repo.findAll().stream().map(SimpleMappers::toUserResponse).toList();
    }
}
