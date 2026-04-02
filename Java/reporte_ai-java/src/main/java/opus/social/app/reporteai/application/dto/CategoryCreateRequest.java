package opus.social.app.reporteai.application.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryCreateRequest {
    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private String color;
    private String iconUrl;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
}
