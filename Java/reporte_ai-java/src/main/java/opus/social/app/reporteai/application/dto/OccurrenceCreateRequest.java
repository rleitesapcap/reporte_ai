package opus.social.app.reporteai.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.math.BigDecimal;

public class OccurrenceCreateRequest {
    private UUID userId;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    private UUID subCategoryId;

    @NotBlank(message = "Description is required")
    private String description;

    private String neighborhood;
    private String referencePoint;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(message = "Severity is required")
    private Integer severity;

    private Integer frequency = 1;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public UUID getSubCategoryId() { return subCategoryId; }
    public void setSubCategoryId(UUID subCategoryId) { this.subCategoryId = subCategoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public String getReferencePoint() { return referencePoint; }
    public void setReferencePoint(String referencePoint) { this.referencePoint = referencePoint; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public Integer getSeverity() { return severity; }
    public void setSeverity(Integer severity) { this.severity = severity; }

    public Integer getFrequency() { return frequency; }
    public void setFrequency(Integer frequency) { this.frequency = frequency; }
}
