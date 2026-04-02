package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.UserConsent;
import opus.social.app.reporteai.domain.port.UserConsentRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.UserConsentJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.UserConsentJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class UserConsentRepositoryAdapter implements UserConsentRepositoryPort {
    private final UserConsentJpaRepository repository;

    public UserConsentRepositoryAdapter(UserConsentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserConsent save(UserConsent consent) {
        return toUserConsent(repository.save(toDomainEntity(consent)));
    }

    @Override
    public Optional<UserConsent> findById(UUID id) {
        return repository.findById(id).map(this::toUserConsent);
    }

    @Override
    public List<UserConsent> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream().map(this::toUserConsent).toList();
    }

    @Override
    public List<UserConsent> findByUserIdAndType(UUID userId, String consentType) {
        return repository.findByUserIdAndConsentType(userId, consentType).stream()
            .map(this::toUserConsent).toList();
    }

    @Override
    public UserConsent update(UserConsent consent) {
        return save(consent);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private UserConsent toUserConsent(UserConsentJpaEntity entity) {
        return new UserConsent(entity.getId(), entity.getUserId(), entity.getConsentType(),
            entity.getAccepted(), entity.getConsentDate(), entity.getDocumentVersion(),
            entity.getIpAddress());
    }

    private UserConsentJpaEntity toDomainEntity(UserConsent consent) {
        return new UserConsentJpaEntity(consent.getId(), consent.getUserId(),
            consent.getConsentType(), consent.getAccepted(), consent.getConsentDate(),
            consent.getDocumentVersion(), consent.getIpAddress());
    }
}
