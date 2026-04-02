package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.SystemSettings;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface SystemSettingsRepositoryPort {
    SystemSettings save(SystemSettings settings);
    Optional<SystemSettings> findById(UUID id);
    Optional<SystemSettings> findByKey(String key);
    List<SystemSettings> findAll();
    SystemSettings update(SystemSettings settings);
    void delete(UUID id);
}
