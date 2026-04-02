package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.SystemSettingsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SystemSettingsJpaRepository extends JpaRepository<SystemSettingsJpaEntity, UUID> {
    Optional<SystemSettingsJpaEntity> findBySettingKey(String key);
}
