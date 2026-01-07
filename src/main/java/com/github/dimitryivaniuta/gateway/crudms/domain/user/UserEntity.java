package com.github.dimitryivaniuta.gateway.crudms.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * users table entity.
 * - Matches Flyway V1__init.sql columns and constraints.
 * - Uses optimistic locking via @Version.
 * - Maintains createdAt/updatedAt automatically.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email", unique = true)
        }
)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email is stored normalized (trim + lower-case) by service layer.
     */
    @Column(name = "email", nullable = false, length = 320, unique = true)
    private String email;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
