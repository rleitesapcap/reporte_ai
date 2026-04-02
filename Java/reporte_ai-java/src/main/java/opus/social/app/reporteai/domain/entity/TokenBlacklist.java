package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio para tokens revogados
 */
public class TokenBlacklist {
    private final UUID id;
    private String tokenJti;
    private UUID userId;
    private String tokenType;
    private LocalDateTime expiresAt;
    private LocalDateTime blacklistedAt;
    private String reason;

    public TokenBlacklist(
        UUID id,
        String tokenJti,
        UUID userId,
        String tokenType,
        LocalDateTime expiresAt,
        LocalDateTime blacklistedAt,
        String reason
    ) {
        this.id = id;
        this.tokenJti = tokenJti;
        this.userId = userId;
        this.tokenType = tokenType;
        this.expiresAt = expiresAt;
        this.blacklistedAt = blacklistedAt;
        this.reason = reason;
    }

    public UUID getId() {
        return id;
    }

    public String getTokenJti() {
        return tokenJti;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenType() {
        return tokenType;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getBlacklistedAt() {
        return blacklistedAt;
    }

    public String getReason() {
        return reason;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
