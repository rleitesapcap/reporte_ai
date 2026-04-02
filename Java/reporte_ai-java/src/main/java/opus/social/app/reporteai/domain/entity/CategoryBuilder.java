package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class CategoryBuilder {
    private UUID id;
    private String name;
    private String description;
    private String color;
    private String iconUrl;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CategoryBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public CategoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CategoryBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CategoryBuilder color(String color) {
        this.color = color;
        return this;
    }

    public CategoryBuilder iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public CategoryBuilder isActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public CategoryBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public CategoryBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Category build() {
        validateRequired();
        return new Category(
            id,
            name,
            description,
            color,
            iconUrl,
            isActive,
            createdAt,
            updatedAt
        );
    }

    private void validateRequired() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
    }
}
