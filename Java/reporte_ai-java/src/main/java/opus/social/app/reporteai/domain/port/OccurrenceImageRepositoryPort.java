package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.OccurrenceImage;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface OccurrenceImageRepositoryPort {
    OccurrenceImage save(OccurrenceImage image);
    Optional<OccurrenceImage> findById(UUID id);
    List<OccurrenceImage> findByOccurrenceId(UUID occurrenceId);
    OccurrenceImage update(OccurrenceImage image);
    void delete(UUID id);
    void deleteByOccurrenceId(UUID occurrenceId);
}
