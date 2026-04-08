package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidade JPA para persistência de usuários de autenticação
 */
@Entity
@Table(name = "auth_users")
public class AuthUserJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, columnDefinition = "text")
    private String passwordHash;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "auth_user_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<AuthRoleJpaEntity> roles = new HashSet<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    public AuthUserJpaEntity() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String username;
        private String email;
        private String passwordHash;
        private String fullName;
        private Set<AuthRoleJpaEntity> roles = new HashSet<>();
        private Boolean isActive;
        private Boolean isLocked;
        private Integer failedLoginAttempts;
        private LocalDateTime lockedUntil;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder roles(Set<AuthRoleJpaEntity> roles) { this.roles = roles; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder isLocked(Boolean isLocked) { this.isLocked = isLocked; return this; }
        public Builder failedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; return this; }
        public Builder lockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; return this; }

        public AuthUserJpaEntity build() {
            AuthUserJpaEntity e = new AuthUserJpaEntity();
            e.id = this.id;
            e.username = this.username;
            e.email = this.email;
            e.passwordHash = this.passwordHash;
            e.fullName = this.fullName;
            e.roles = this.roles;
            e.isActive = this.isActive;
            e.isLocked = this.isLocked;
            e.failedLoginAttempts = this.failedLoginAttempts;
            e.lockedUntil = this.lockedUntil;
            return e;
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Set<AuthRoleJpaEntity> getRoles() { return roles; }
    public void setRoles(Set<AuthRoleJpaEntity> roles) { this.roles = roles; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
}
