package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.DeduplicationRecordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DeduplicationRecordJpaRepository extends JpaRepository<DeduplicationRecordJpaEntity, UUID> {
    List<DeduplicationRecordJpaEntity> findByMainOccurrenceId(UUID mainOccurrenceId);
    List<DeduplicationRecordJpaEntity> findByDuplicateOccurrenceId(UUID duplicateOccurrenceId);
    List<DeduplicationRecordJpaEntity> findByDedupMethod(String method);
}
