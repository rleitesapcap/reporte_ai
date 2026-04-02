package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class Occurrence {
    private final UUID id;
    private UUID userId;
    private final UUID categoryId;
    private UUID subCategoryId;
    private final String protocolId;
    private String description;
    private String additionalNotes;
    private String neighborhood;
    private String referencePoint;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer severity;
    private Integer frequency;
    private BigDecimal priorityScore;
    private Integer recurrenceCount;
    private Boolean hasPhoto;
    private Integer photoCount;
    private String status;
    private BigDecimal confidenceLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private Boolean isDuplicate;
    private UUID duplicateMainOccurrenceId;

    public Occurrence(
        UUID id,
        UUID userId,
        UUID categoryId,
        UUID subCategoryId,
        String protocolId,
        String description,
        String additionalNotes,
        String neighborhood,
        String referencePoint,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer severity,
        Integer frequency,
        BigDecimal priorityScore,
        Integer recurrenceCount,
        Boolean hasPhoto,
        Integer photoCount,
        String status,
        BigDecimal confidenceLevel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime resolvedAt,
        Boolean isDuplicate,
        UUID duplicateMainOccurrenceId
    ) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.protocolId = protocolId;
        this.description = description;
        this.additionalNotes = additionalNotes;
        this.neighborhood = neighborhood;
        this.referencePoint = referencePoint;
        this.latitude = latitude;
        this.longitude = longitude;
        this.severity = severity;
        this.frequency = frequency;
        this.priorityScore = priorityScore;
        this.recurrenceCount = recurrenceCount;
        this.hasPhoto = hasPhoto;
        this.photoCount = photoCount;
        this.status = status;
        this.confidenceLevel = confidenceLevel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
        this.isDuplicate = isDuplicate;
        this.duplicateMainOccurrenceId = duplicateMainOccurrenceId;
    }

    public static OccurrenceBuilder builder() {
        return new OccurrenceBuilder();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public UUID getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(UUID subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getProtocolId() {
        return protocolId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getReferencePoint() {
        return referencePoint;
    }

    public void setReferencePoint(String referencePoint) {
        this.referencePoint = referencePoint;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public BigDecimal getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(BigDecimal priorityScore) {
        this.priorityScore = priorityScore;
    }

    public Integer getRecurrenceCount() {
        return recurrenceCount;
    }

    public void setRecurrenceCount(Integer recurrenceCount) {
        this.recurrenceCount = recurrenceCount;
    }

    public Boolean getHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(Boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(BigDecimal confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Boolean getIsDuplicate() {
        return isDuplicate;
    }

    public void setIsDuplicate(Boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    public UUID getDuplicateMainOccurrenceId() {
        return duplicateMainOccurrenceId;
    }

    public void setDuplicateMainOccurrenceId(UUID duplicateMainOccurrenceId) {
        this.duplicateMainOccurrenceId = duplicateMainOccurrenceId;
    }
}
