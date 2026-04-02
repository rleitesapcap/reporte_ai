package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.DeduplicationRecord;
import opus.social.app.reporteai.domain.port.DeduplicationRecordRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.DeduplicationRecordJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.DeduplicationRecordJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class DeduplicationRecordRepositoryAdapter implements DeduplicationRecordRepositoryPort {
    private final DeduplicationRecordJpaRepository repository;

    public DeduplicationRecordRepositoryAdapter(DeduplicationRecordJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeduplicationRecord save(DeduplicationRecord record) {
        return toRecord(repository.save(toEntity(record)));
    }

    @Override
    public Optional<DeduplicationRecord> findById(UUID id) {
        return repository.findById(id).map(this::toRecord);
    }

    @Override
    public List<DeduplicationRecord> findByMainOccurrenceId(UUID mainOccurrenceId) {
        return repository.findByMainOccurrenceId(mainOccurrenceId).stream()
            .map(this::toRecord).toList();
    }

    @Override
    public List<DeduplicationRecord> findByDuplicateOccurrenceId(UUID duplicateOccurrenceId) {
        return repository.findByDuplicateOccurrenceId(duplicateOccurrenceId).stream()
            .map(this::toRecord).toList();
    }

    @Override
    public List<DeduplicationRecord> findByMethod(String method) {
        return repository.findByDedupMethod(method).stream().map(this::toRecord).toList();
    }

    @Override
    public DeduplicationRecord update(DeduplicationRecord record) {
        return save(record);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private DeduplicationRecord toRecord(DeduplicationRecordJpaEntity entity) {
        return new DeduplicationRecord(entity.getId(), entity.getMainOccurrenceId(),
            entity.getDuplicateOccurrenceId(), entity.getSimilarityScore(),
            entity.getGeographicDistanceMeters(), entity.getTimeDifferenceMinutes(),
            entity.getDedupReason(), entity.getDedupMethod(), entity.getCreatedAt());
    }

    private DeduplicationRecordJpaEntity toEntity(DeduplicationRecord record) {
        return new DeduplicationRecordJpaEntity(record.getId(), record.getMainOccurrenceId(),
            record.getDuplicateOccurrenceId(), record.getSimilarityScore(),
            record.getGeographicDistanceMeters(), record.getTimeDifferenceMinutes(),
            record.getDedupReason(), record.getDedupMethod(), record.getCreatedAt());
    }
}
