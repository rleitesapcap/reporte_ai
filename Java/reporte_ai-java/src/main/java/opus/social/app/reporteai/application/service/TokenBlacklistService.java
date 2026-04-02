package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.infrastructure.persistence.entity.TokenBlacklistJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.TokenBlacklistRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Serviço para gerenciar blacklist de tokens
 * Revoga e rastreia tokens inválidos
 */
@Service
@Transactional
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    public TokenBlacklistService(TokenBlacklistRepository tokenBlacklistRepository) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    /**
     * Adicionar token ao blacklist
     */
    public void blacklistToken(String tokenJti, UUID userId, String tokenType, LocalDateTime expiresAt, String reason) {
        TokenBlacklistJpaEntity blacklistedToken = TokenBlacklistJpaEntity.builder()
            .id(UUID.randomUUID())
            .tokenJti(tokenJti)
            .userId(userId)
            .tokenType(tokenType)
            .expiresAt(expiresAt)
            .reason(reason)
            .build();

        tokenBlacklistRepository.save(blacklistedToken);
    }

    /**
     * Verificar se token está no blacklist
     */
    public boolean isTokenBlacklisted(String tokenJti) {
        return tokenBlacklistRepository.isTokenBlacklisted(tokenJti);
    }

    /**
     * Adicionar ao blacklist para logout
     */
    public void blacklistTokenForLogout(String tokenJti, UUID userId, String tokenType, LocalDateTime expiresAt) {
        blacklistToken(tokenJti, userId, tokenType, expiresAt, "LOGOUT");
    }

    /**
     * Limpar tokens expirados do blacklist
     * Executado a cada 1 hora
     */
    @Scheduled(fixedRateString = "${app.tokenBlacklist.cleanup.interval:3600000}")
    public void cleanupExpiredTokens() {
        tokenBlacklistRepository.deleteExpiredTokens();
    }

    /**
     * Adicionar ao blacklist por mudança de senha
     */
    public void blacklistTokenForPasswordChange(String tokenJti, UUID userId, LocalDateTime expiresAt) {
        blacklistToken(tokenJti, userId, "ACCESS", expiresAt, "PASSWORD_CHANGED");
    }

    /**
     * Adicionar ao blacklist por remoção de role
     */
    public void blacklistTokenForRoleChange(String tokenJti, UUID userId, LocalDateTime expiresAt) {
        blacklistToken(tokenJti, userId, "ACCESS", expiresAt, "ROLE_CHANGED");
    }

    /**
     * Remover todos os tokens do usuário
     */
    public void revokeAllUserTokens(UUID userId) {
        tokenBlacklistRepository.findAllByUserId(userId).forEach(token -> {
            token.setReason("ALL_REVOKED");
            tokenBlacklistRepository.save(token);
        });
    }
}
