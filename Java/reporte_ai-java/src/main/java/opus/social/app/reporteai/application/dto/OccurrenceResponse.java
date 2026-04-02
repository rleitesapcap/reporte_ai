package opus.social.app.reporteai.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class OccurrenceResponse {
    private UUID id;
    private String protocolId;
    private UUID categoryId;
    private String description;
    private String neighborhood;
    private Integer severity;
    private Integer frequency;
    private BigDecimal priorityScore;
    private String status;
    private Integer photoCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OccurrenceResponse() {}

    public OccurrenceResponse(UUID id, String protocolId, UUID categoryId, String description,
            String neighborhood, Integer severity, Integer frequency, BigDecimal priorityScore,
            String status, Integer photoCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.protocolId = protocolId;
        this.categoryId = categoryId;
        this.description = description;
        this.neighborhood = neighborhood;
        this.severity = severity;
        this.frequency = frequency;
        this.priorityScore = priorityScore;
        this.status = status;
        this.photoCount = photoCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getProtocolId() { return protocolId; }
    public void setProtocolId(String protocolId) { this.protocolId = protocolId; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public Integer getSeverity() { return severity; }
    public void setSeverity(Integer severity) { this.severity = severity; }

    public Integer getFrequency() { return frequency; }
    public void setFrequency(Integer frequency) { this.frequency = frequency; }

    public BigDecimal getPriorityScore() { return priorityScore; }
    public void setPriorityScore(BigDecimal priorityScore) { this.priorityScore = priorityScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getPhotoCount() { return photoCount; }
    public void setPhotoCount(Integer photoCount) { this.photoCount = photoCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
