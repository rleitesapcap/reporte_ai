package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.UserRateLimitJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRateLimitJpaRepository extends JpaRepository<UserRateLimitJpaEntity, UUID> {
    Optional<UserRateLimitJpaEntity> findByUserId(UUID userId);
}
