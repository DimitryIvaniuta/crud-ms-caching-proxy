package com.github.dimitryivaniuta.gateway.crudms.domain.user;

import com.github.dimitryivaniuta.gateway.crudms.web.dto.user.UserCreateRequest;
import com.github.dimitryivaniuta.gateway.crudms.web.dto.user.UserResponse;
import com.github.dimitryivaniuta.gateway.crudms.web.dto.user.UserUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST API for users CRUD.
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody UserCreateRequest req,
            UriComponentsBuilder uriBuilder
    ) {
        UserResponse created = userService.create(req);
        URI location = uriBuilder.path("/api/users/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable long id) {
        return ResponseEntity.ok(userService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(userService.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable long id,
            @Valid @RequestBody UserUpdateRequest req
    ) {
        return ResponseEntity.ok(userService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
