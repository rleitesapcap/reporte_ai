package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.UserRateLimit;
import opus.social.app.reporteai.domain.port.UserRateLimitRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.UserRateLimitJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.UserRateLimitJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRateLimitRepositoryAdapter implements UserRateLimitRepositoryPort {
    private final UserRateLimitJpaRepository repository;

    public UserRateLimitRepositoryAdapter(UserRateLimitJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserRateLimit save(UserRateLimit rateLimit) {
        return toRateLimit(repository.save(toEntity(rateLimit)));
    }

    @Override
    public Optional<UserRateLimit> findByUserId(UUID userId) {
        return repository.findByUserId(userId).map(this::toRateLimit);
    }

    @Override
    public UserRateLimit update(UserRateLimit rateLimit) {
        return save(rateLimit);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private UserRateLimit toRateLimit(UserRateLimitJpaEntity entity) {
        return new UserRateLimit(entity.getId(), entity.getUserId(), entity.getDailyLimit(),
            entity.getHourlyLimit(), entity.getOccurrencesToday(),
            entity.getOccurrencesThisHour(), entity.getLastResetDate(),
            entity.getLastResetHour(), entity.getIsBlocked(), entity.getBlockedUntil(),
            entity.getUpdatedAt());
    }

    private UserRateLimitJpaEntity toEntity(UserRateLimit rateLimit) {
        return new UserRateLimitJpaEntity(rateLimit.getId(), rateLimit.getUserId(),
            rateLimit.getDailyLimit(), rateLimit.getHourlyLimit(),
            rateLimit.getOccurrencesToday(), rateLimit.getOccurrencesThisHour(),
            rateLimit.getLastResetDate(), rateLimit.getLastResetHour(),
            rateLimit.getIsBlocked(), rateLimit.getBlockedUntil(),
            rateLimit.getUpdatedAt());
    }
}
