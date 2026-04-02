package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.SystemSettings;
import opus.social.app.reporteai.domain.port.SystemSettingsRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.SystemSettingsJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.SystemSettingsJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class SystemSettingsRepositoryAdapter implements SystemSettingsRepositoryPort {
    private final SystemSettingsJpaRepository repository;

    public SystemSettingsRepositoryAdapter(SystemSettingsJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public SystemSettings save(SystemSettings settings) {
        return toSettings(repository.save(toEntity(settings)));
    }

    @Override
    public Optional<SystemSettings> findById(UUID id) {
        return repository.findById(id).map(this::toSettings);
    }

    @Override
    public Optional<SystemSettings> findByKey(String key) {
        return repository.findBySettingKey(key).map(this::toSettings);
    }

    @Override
    public List<SystemSettings> findAll() {
        return repository.findAll().stream().map(this::toSettings).toList();
    }

    @Override
    public SystemSettings update(SystemSettings settings) {
        return save(settings);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private SystemSettings toSettings(SystemSettingsJpaEntity entity) {
        return new SystemSettings(entity.getId(), entity.getSettingKey(),
            entity.getSettingValue(), entity.getSettingType(), entity.getDescription(),
            entity.getUpdatedAt());
    }

    private SystemSettingsJpaEntity toEntity(SystemSettings settings) {
        return new SystemSettingsJpaEntity(settings.getId(), settings.getSettingKey(),
            settings.getSettingValue(), settings.getSettingType(),
            settings.getDescription(), settings.getUpdatedAt());
    }
}
