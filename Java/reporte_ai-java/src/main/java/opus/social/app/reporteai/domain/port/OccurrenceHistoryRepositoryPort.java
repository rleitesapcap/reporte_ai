package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.OccurrenceHistory;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface OccurrenceHistoryRepositoryPort {
    OccurrenceHistory save(OccurrenceHistory history);
    Optional<OccurrenceHistory> findById(UUID id);
    List<OccurrenceHistory> findByOccurrenceId(UUID occurrenceId);
    List<OccurrenceHistory> findByAction(String action);
    OccurrenceHistory update(OccurrenceHistory history);
    void delete(UUID id);
}
