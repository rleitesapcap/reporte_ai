package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.SystemSettings;
import opus.social.app.reporteai.domain.port.SystemSettingsRepositoryPort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SystemSettingsApplicationService {
    private final SystemSettingsRepositoryPort settingsRepository;

    public SystemSettingsApplicationService(SystemSettingsRepositoryPort settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public SystemSettings saveSetting(String key, String value, String type, String description) {
        SystemSettings settings = new SystemSettings(UUID.randomUUID(), key, value, type,
            description, LocalDateTime.now());
        return settingsRepository.save(settings);
    }

    public SystemSettings getSetting(String key) {
        return settingsRepository.findByKey(key)
            .orElseThrow(() -> new RuntimeException("Setting not found: " + key));
    }

    public List<SystemSettings> getAllSettings() {
        return settingsRepository.findAll();
    }

    public SystemSettings updateSetting(String key, String value) {
        SystemSettings settings = getSetting(key);
        settings.setSettingValue(value);
        settings.setUpdatedAt(LocalDateTime.now());
        return settingsRepository.update(settings);
    }

    public void deleteSetting(UUID id) {
        settingsRepository.delete(id);
    }
}
