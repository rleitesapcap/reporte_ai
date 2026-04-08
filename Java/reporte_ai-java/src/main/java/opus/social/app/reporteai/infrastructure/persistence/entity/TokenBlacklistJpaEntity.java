package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA para tokens revogados/bloqueados
 */
@Entity
@Table(name = "token_blacklist")
public class TokenBlacklistJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "token_jti", nullable = false, unique = true, length = 500)
    private String tokenJti;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "token_type", nullable = false, length = 50)
    private String tokenType;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "blacklisted_at", nullable = false)
    private LocalDateTime blacklistedAt;

    @Column(name = "reason", length = 500)
    private String reason;

    public TokenBlacklistJpaEntity() {}

    @PrePersist
    protected void onCreate() {
        if (blacklistedAt == null) {
            blacklistedAt = LocalDateTime.now();
        }
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String tokenJti;
        private UUID userId;
        private String tokenType;
        private LocalDateTime expiresAt;
        private String reason;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder tokenJti(String tokenJti) { this.tokenJti = tokenJti; return this; }
        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }

        public TokenBlacklistJpaEntity build() {
            TokenBlacklistJpaEntity e = new TokenBlacklistJpaEntity();
            e.id = this.id;
            e.tokenJti = this.tokenJti;
            e.userId = this.userId;
            e.tokenType = this.tokenType;
            e.expiresAt = this.expiresAt;
            e.reason = this.reason;
            return e;
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTokenJti() { return tokenJti; }
    public void setTokenJti(String tokenJti) { this.tokenJti = tokenJti; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getBlacklistedAt() { return blacklistedAt; }
    public void setBlacklistedAt(LocalDateTime blacklistedAt) { this.blacklistedAt = blacklistedAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
