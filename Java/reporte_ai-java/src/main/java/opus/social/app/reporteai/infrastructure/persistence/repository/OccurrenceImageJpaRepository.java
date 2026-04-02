package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.OccurrenceImageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OccurrenceImageJpaRepository extends JpaRepository<OccurrenceImageJpaEntity, UUID> {
    List<OccurrenceImageJpaEntity> findByOccurrenceId(UUID occurrenceId);
    void deleteByOccurrenceId(UUID occurrenceId);
}
