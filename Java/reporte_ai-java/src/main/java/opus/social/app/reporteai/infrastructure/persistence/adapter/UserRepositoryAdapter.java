package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.User;
import opus.social.app.reporteai.domain.port.UserRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.UserJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository repository;

    public UserRepositoryAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = toDomainEntity(user);
        UserJpaEntity saved = repository.save(entity);
        return toUser(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(this::toUser);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return repository.findByPhoneNumber(phoneNumber).map(this::toUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toUser);
    }

    @Override
    public List<User> findAllActive() {
        return repository.findAllActive().stream()
            .map(this::toUser)
            .toList();
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream()
            .map(this::toUser)
            .toList();
    }

    @Override
    public User update(User user) {
        return save(user);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return repository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    private User toUser(UserJpaEntity entity) {
        return User.builder()
            .id(entity.getId())
            .phoneNumber(entity.getPhoneNumber())
            .name(entity.getName())
            .email(entity.getEmail())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .lastInteraction(entity.getLastInteraction())
            .trustScore(entity.getTrustScore())
            .totalOccurrences(entity.getTotalOccurrences())
            .occurrencesWithPhoto(entity.getOccurrencesWithPhoto())
            .isActive(entity.getIsActive())
            .anonymized(entity.getAnonymized())
            .anonymizedAt(entity.getAnonymizedAt())
            .build();
    }

    private UserJpaEntity toDomainEntity(User user) {
        return new UserJpaEntity(
            user.getId(),
            user.getPhoneNumber(),
            user.getName(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getLastInteraction(),
            user.getTrustScore(),
            user.getTotalOccurrences(),
            user.getOccurrencesWithPhoto(),
            user.getIsActive(),
            user.getAnonymized(),
            user.getAnonymizedAt()
        );
    }
}
