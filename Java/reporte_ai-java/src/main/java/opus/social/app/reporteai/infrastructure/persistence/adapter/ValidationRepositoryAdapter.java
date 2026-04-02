package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.Validation;
import opus.social.app.reporteai.domain.port.ValidationRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.ValidationJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.ValidationJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class ValidationRepositoryAdapter implements ValidationRepositoryPort {
    private final ValidationJpaRepository repository;

    public ValidationRepositoryAdapter(ValidationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Validation save(Validation validation) {
        return toValidation(repository.save(toDomainEntity(validation)));
    }

    @Override
    public Optional<Validation> findById(UUID id) {
        return repository.findById(id).map(this::toValidation);
    }

    @Override
    public List<Validation> findByOccurrenceId(UUID occurrenceId) {
        return repository.findByOccurrenceId(occurrenceId).stream()
            .map(this::toValidation).toList();
    }

    @Override
    public List<Validation> findByResult(String result) {
        return repository.findByResult(result).stream().map(this::toValidation).toList();
    }

    @Override
    public List<Validation> findByValidationType(String validationType) {
        return repository.findByValidationType(validationType).stream()
            .map(this::toValidation).toList();
    }

    @Override
    public Validation update(Validation validation) {
        return save(validation);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private Validation toValidation(ValidationJpaEntity entity) {
        return new Validation(entity.getId(), entity.getOccurrenceId(),
            entity.getValidatorUserId(), entity.getValidationType(), entity.getResult(),
            entity.getReason(), entity.getConfidence(), entity.getMultipleReportsCount(),
            entity.getValidatedAt());
    }

    private ValidationJpaEntity toDomainEntity(Validation validation) {
        return new ValidationJpaEntity(validation.getId(), validation.getOccurrenceId(),
            validation.getValidatorUserId(), validation.getValidationType(),
            validation.getResult(), validation.getReason(), validation.getConfidence(),
            validation.getMultipleReportsCount(), validation.getValidatedAt());
    }
}
