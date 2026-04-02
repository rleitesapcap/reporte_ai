package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.OccurrenceImage;
import opus.social.app.reporteai.domain.port.OccurrenceImageRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.OccurrenceImageJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.OccurrenceImageJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class OccurrenceImageRepositoryAdapter implements OccurrenceImageRepositoryPort {
    private final OccurrenceImageJpaRepository repository;

    public OccurrenceImageRepositoryAdapter(OccurrenceImageJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OccurrenceImage save(OccurrenceImage image) {
        OccurrenceImageJpaEntity saved = repository.save(toDomainEntity(image));
        return toOccurrenceImage(saved);
    }

    @Override
    public Optional<OccurrenceImage> findById(UUID id) {
        return repository.findById(id).map(this::toOccurrenceImage);
    }

    @Override
    public List<OccurrenceImage> findByOccurrenceId(UUID occurrenceId) {
        return repository.findByOccurrenceId(occurrenceId).stream()
            .map(this::toOccurrenceImage).toList();
    }

    @Override
    public OccurrenceImage update(OccurrenceImage image) {
        return save(image);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteByOccurrenceId(UUID occurrenceId) {
        repository.deleteByOccurrenceId(occurrenceId);
    }

    private OccurrenceImage toOccurrenceImage(OccurrenceImageJpaEntity entity) {
        return new OccurrenceImage(entity.getId(), entity.getOccurrenceId(), entity.getS3Url(),
            entity.getS3Key(), entity.getImageSize(), entity.getImageFormat(),
            entity.getUploadedAt(), entity.getProcessed());
    }

    private OccurrenceImageJpaEntity toDomainEntity(OccurrenceImage image) {
        return new OccurrenceImageJpaEntity(image.getId(), image.getOccurrenceId(),
            image.getS3Url(), image.getS3Key(), image.getImageSize(),
            image.getImageFormat(), image.getUploadedAt(), image.getProcessed());
    }
}
