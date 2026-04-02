package opus.social.app.reporteai.application.dto;

import java.util.UUID;

public class CategoryResponse {
    private UUID id;
    private String name;
    private String description;
    private String color;
    private String iconUrl;
    private Boolean isActive;

    public CategoryResponse() {}

    public CategoryResponse(UUID id, String name, String description, String color,
            String iconUrl, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.iconUrl = iconUrl;
        this.isActive = isActive;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
