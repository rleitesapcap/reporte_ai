package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA para tokens revogados/bloqueados
 */
@Entity
@Table(name = "token_blacklist")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @PrePersist
    protected void onCreate() {
        if (blacklistedAt == null) {
            blacklistedAt = LocalDateTime.now();
        }
    }
}
