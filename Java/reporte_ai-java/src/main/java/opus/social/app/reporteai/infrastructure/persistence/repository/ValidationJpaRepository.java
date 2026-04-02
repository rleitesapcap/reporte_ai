package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.ValidationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ValidationJpaRepository extends JpaRepository<ValidationJpaEntity, UUID> {
    List<ValidationJpaEntity> findByOccurrenceId(UUID occurrenceId);
    List<ValidationJpaEntity> findByResult(String result);
    List<ValidationJpaEntity> findByValidationType(String validationType);
}
