package opus.social.app.reporteai.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class SystemSettingsResponse {
    private UUID id;
    private String settingKey;
    private String settingValue;
    private String settingType;
    private String description;
    private LocalDateTime updatedAt;

    public SystemSettingsResponse() {}

    public SystemSettingsResponse(UUID id, String key, String value, String type,
            String description, LocalDateTime updated) {
        this.id = id;
        this.settingKey = key;
        this.settingValue = value;
        this.settingType = type;
        this.description = description;
        this.updatedAt = updated;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String key) { this.settingKey = key; }

    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String value) { this.settingValue = value; }

    public String getSettingType() { return settingType; }
    public void setSettingType(String type) { this.settingType = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updated) { this.updatedAt = updated; }
}
