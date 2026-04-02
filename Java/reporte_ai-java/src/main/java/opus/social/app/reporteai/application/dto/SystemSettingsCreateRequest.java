package opus.social.app.reporteai.application.dto;

import jakarta.validation.constraints.NotBlank;

public class SystemSettingsCreateRequest {
    @NotBlank(message = "Setting key is required")
    private String settingKey;

    private String settingValue;
    private String settingType;
    private String description;

    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String key) { this.settingKey = key; }

    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String value) { this.settingValue = value; }

    public String getSettingType() { return settingType; }
    public void setSettingType(String type) { this.settingType = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
