package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.DeduplicationRecord;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface DeduplicationRecordRepositoryPort {
    DeduplicationRecord save(DeduplicationRecord record);
    Optional<DeduplicationRecord> findById(UUID id);
    List<DeduplicationRecord> findByMainOccurrenceId(UUID mainOccurrenceId);
    List<DeduplicationRecord> findByDuplicateOccurrenceId(UUID duplicateOccurrenceId);
    List<DeduplicationRecord> findByMethod(String method);
    DeduplicationRecord update(DeduplicationRecord record);
    void delete(UUID id);
}
