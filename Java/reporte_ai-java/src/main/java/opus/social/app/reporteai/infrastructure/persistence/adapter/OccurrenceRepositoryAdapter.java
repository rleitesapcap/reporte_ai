package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.Occurrence;
import opus.social.app.reporteai.domain.port.OccurrenceRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.OccurrenceJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.OccurrenceJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class OccurrenceRepositoryAdapter implements OccurrenceRepositoryPort {
    private final OccurrenceJpaRepository repository;

    public OccurrenceRepositoryAdapter(OccurrenceJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Occurrence save(Occurrence occurrence) {
        OccurrenceJpaEntity entity = toDomainEntity(occurrence);
        OccurrenceJpaEntity saved = repository.save(entity);
        return toOccurrence(saved);
    }

    @Override
    public Optional<Occurrence> findById(UUID id) {
        return repository.findById(id).map(this::toOccurrence);
    }

    @Override
    public Optional<Occurrence> findByProtocolId(String protocolId) {
        return repository.findByProtocolId(protocolId).map(this::toOccurrence);
    }

    @Override
    public List<Occurrence> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
            .map(this::toOccurrence)
            .toList();
    }

    @Override
    public List<Occurrence> findByCategoryId(UUID categoryId) {
        return repository.findByCategoryId(categoryId).stream()
            .map(this::toOccurrence)
            .toList();
    }

    @Override
    public List<Occurrence> findByNeighborhood(String neighborhood) {
        return repository.findByNeighborhood(neighborhood).stream()
            .map(this::toOccurrence)
            .toList();
    }

    @Override
    public List<Occurrence> findByStatus(String status) {
        return repository.findByStatus(status).stream()
            .map(this::toOccurrence)
            .toList();
    }

    @Override
    public List<Occurrence> findByStatusOrderByPriority(String status) {
        return repository.findByStatusOrderByPriority(status).stream()
            .map(this::toOccurrence)
            .toList();
    }

    @Override
    public List<Occurrence> findRecent(int limit) {
        return repository.findRecent(limit).stream()
            .map(this::toOccurrence)
            .toList();
    }

    @Override
    public List<Occurrence> findAll() {
        return repository.findAll().stream()
            .map(this::toOccurrence)
            .toList();
    }

    @Override
    public Occurrence update(Occurrence occurrence) {
        return save(occurrence);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<Occurrence> findDuplicates(UUID occurrenceId) {
        return repository.findDuplicates(occurrenceId).stream()
            .map(this::toOccurrence)
            .toList();
    }

    @Override
    public long countByStatus(String status) {
        return repository.countByStatus(status);
    }

    @Override
    public long countByCategory(UUID categoryId) {
        return repository.countByCategoryId(categoryId);
    }

    private Occurrence toOccurrence(OccurrenceJpaEntity entity) {
        return Occurrence.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .categoryId(entity.getCategoryId())
            .subCategoryId(entity.getSubCategoryId())
            .protocolId(entity.getProtocolId())
            .description(entity.getDescription())
            .additionalNotes(entity.getAdditionalNotes())
            .neighborhood(entity.getNeighborhood())
            .referencePoint(entity.getReferencePoint())
            .latitude(entity.getLatitude())
            .longitude(entity.getLongitude())
            .severity(entity.getSeverity())
            .frequency(entity.getFrequency())
            .priorityScore(entity.getPriorityScore())
            .recurrenceCount(entity.getRecurrenceCount())
            .hasPhoto(entity.getHasPhoto())
            .photoCount(entity.getPhotoCount())
            .status(entity.getStatus())
            .confidenceLevel(entity.getConfidenceLevel())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .resolvedAt(entity.getResolvedAt())
            .isDuplicate(entity.getIsDuplicate())
            .duplicateMainOccurrenceId(entity.getDuplicateMainOccurrenceId())
            .build();
    }

    private OccurrenceJpaEntity toDomainEntity(Occurrence occurrence) {
        return new OccurrenceJpaEntity(
            occurrence.getId(),
            occurrence.getUserId(),
            occurrence.getCategoryId(),
            occurrence.getSubCategoryId(),
            occurrence.getProtocolId(),
            occurrence.getDescription(),
            occurrence.getAdditionalNotes(),
            occurrence.getNeighborhood(),
            occurrence.getReferencePoint(),
            occurrence.getLatitude(),
            occurrence.getLongitude(),
            occurrence.getSeverity(),
            occurrence.getFrequency(),
            occurrence.getPriorityScore(),
            occurrence.getRecurrenceCount(),
            occurrence.getHasPhoto(),
            occurrence.getPhotoCount(),
            occurrence.getStatus(),
            occurrence.getConfidenceLevel(),
            occurrence.getCreatedAt(),
            occurrence.getUpdatedAt(),
            occurrence.getResolvedAt(),
            occurrence.getIsDuplicate(),
            occurrence.getDuplicateMainOccurrenceId()
        );
    }
}
