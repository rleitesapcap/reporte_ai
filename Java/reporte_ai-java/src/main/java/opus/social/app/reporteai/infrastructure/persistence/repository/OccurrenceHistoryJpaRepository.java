package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.OccurrenceHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OccurrenceHistoryJpaRepository extends JpaRepository<OccurrenceHistoryJpaEntity, UUID> {
    List<OccurrenceHistoryJpaEntity> findByOccurrenceId(UUID occurrenceId);
    List<OccurrenceHistoryJpaEntity> findByAction(String action);
}
