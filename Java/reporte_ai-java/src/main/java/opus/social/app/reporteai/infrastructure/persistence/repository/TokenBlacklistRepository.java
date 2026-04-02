package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.TokenBlacklistJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para token blacklist (revogação de tokens)
 */
@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistJpaEntity, UUID> {
    
    Optional<TokenBlacklistJpaEntity> findByTokenJti(String tokenJti);
    
    List<TokenBlacklistJpaEntity> findAllByUserId(UUID userId);
    
    @Query("DELETE FROM TokenBlacklistJpaEntity t WHERE t.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
    
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TokenBlacklistJpaEntity t WHERE t.tokenJti = :tokenJti")
    boolean isTokenBlacklisted(@Param("tokenJti") String tokenJti);
    
    @Query("SELECT t FROM TokenBlacklistJpaEntity t WHERE t.expiresAt < :date")
    List<TokenBlacklistJpaEntity> findExpiredTokens(@Param("date") LocalDateTime date);
}
