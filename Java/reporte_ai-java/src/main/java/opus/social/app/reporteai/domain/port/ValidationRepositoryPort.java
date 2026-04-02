package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.Validation;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface ValidationRepositoryPort {
    Validation save(Validation validation);
    Optional<Validation> findById(UUID id);
    List<Validation> findByOccurrenceId(UUID occurrenceId);
    List<Validation> findByResult(String result);
    List<Validation> findByValidationType(String validationType);
    Validation update(Validation validation);
    void delete(UUID id);
}
