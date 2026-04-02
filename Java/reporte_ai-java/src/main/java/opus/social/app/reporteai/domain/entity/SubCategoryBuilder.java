package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubCategoryBuilder {
    private UUID id;
    private UUID categoryId;
    private String name;
    private String description;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SubCategoryBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public SubCategoryBuilder categoryId(UUID categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public SubCategoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public SubCategoryBuilder description(String description) {
        this.description = description;
        return this;
    }

    public SubCategoryBuilder isActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public SubCategoryBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public SubCategoryBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public SubCategory build() {
        validateRequired();
        return new SubCategory(
            id,
            categoryId,
            name,
            description,
            isActive,
            createdAt,
            updatedAt
        );
    }

    private void validateRequired() {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("SubCategory name is required");
        }
    }
}
