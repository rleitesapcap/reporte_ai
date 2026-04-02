package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.UserConsentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserConsentJpaRepository extends JpaRepository<UserConsentJpaEntity, UUID> {
    List<UserConsentJpaEntity> findByUserId(UUID userId);
    List<UserConsentJpaEntity> findByUserIdAndConsentType(UUID userId, String consentType);
}
