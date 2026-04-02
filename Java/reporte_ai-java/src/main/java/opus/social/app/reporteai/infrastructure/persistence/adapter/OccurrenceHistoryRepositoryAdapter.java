package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.OccurrenceHistory;
import opus.social.app.reporteai.domain.port.OccurrenceHistoryRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.OccurrenceHistoryJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.OccurrenceHistoryJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class OccurrenceHistoryRepositoryAdapter implements OccurrenceHistoryRepositoryPort {
    private final OccurrenceHistoryJpaRepository repository;

    public OccurrenceHistoryRepositoryAdapter(OccurrenceHistoryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OccurrenceHistory save(OccurrenceHistory history) {
        return toHistory(repository.save(toEntity(history)));
    }

    @Override
    public Optional<OccurrenceHistory> findById(UUID id) {
        return repository.findById(id).map(this::toHistory);
    }

    @Override
    public List<OccurrenceHistory> findByOccurrenceId(UUID occurrenceId) {
        return repository.findByOccurrenceId(occurrenceId).stream()
            .map(this::toHistory).toList();
    }

    @Override
    public List<OccurrenceHistory> findByAction(String action) {
        return repository.findByAction(action).stream().map(this::toHistory).toList();
    }

    @Override
    public OccurrenceHistory update(OccurrenceHistory history) {
        return save(history);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private OccurrenceHistory toHistory(OccurrenceHistoryJpaEntity entity) {
        return new OccurrenceHistory(entity.getId(), entity.getOccurrenceId(),
            entity.getChangedByUserId(), entity.getAction(), entity.getOldStatus(),
            entity.getNewStatus(), entity.getOldPriorityScore(),
            entity.getNewPriorityScore(), entity.getChangeReason(), entity.getCreatedAt());
    }

    private OccurrenceHistoryJpaEntity toEntity(OccurrenceHistory history) {
        return new OccurrenceHistoryJpaEntity(history.getId(), history.getOccurrenceId(),
            history.getChangedByUserId(), history.getAction(), history.getOldStatus(),
            history.getNewStatus(), history.getOldPriorityScore(),
            history.getNewPriorityScore(), history.getChangeReason(), history.getCreatedAt());
    }
}
