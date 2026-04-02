package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(
    name = "occurrences",
    indexes = {
        @Index(name = "idx_occurrences_user_id", columnList = "user_id"),
        @Index(name = "idx_occurrences_category_id", columnList = "category_id"),
        @Index(name = "idx_occurrences_sub_category_id", columnList = "sub_category_id"),
        @Index(name = "idx_occurrences_status", columnList = "status"),
        @Index(name = "idx_occurrences_created_at", columnList = "created_at"),
        @Index(name = "idx_occurrences_priority_score", columnList = "priority_score DESC"),
        @Index(name = "idx_occurrences_protocol_id", columnList = "protocol_id"),
        @Index(name = "idx_occurrences_neighborhood", columnList = "neighborhood"),
        @Index(name = "idx_occurrences_is_duplicate", columnList = "is_duplicate")
    }
)
public class OccurrenceJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private UUID userId;

    @Column(nullable = false)
    private UUID categoryId;

    @Column
    private UUID subCategoryId;

    @Column(nullable = false, unique = true, length = 50)
    private String protocolId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String additionalNotes;

    @Column(length = 255)
    private String neighborhood;

    @Column(length = 500)
    private String referencePoint;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column
    private Integer severity = 1;

    @Column
    private Integer frequency = 1;

    @Column(precision = 8, scale = 2)
    private BigDecimal priorityScore;

    @Column
    private Integer recurrenceCount = 0;

    @Column
    private Boolean hasPhoto = false;

    @Column
    private Integer photoCount = 0;

    @Column(length = 50)
    private String status = "received";

    @Column(precision = 4, scale = 2)
    private BigDecimal confidenceLevel;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime resolvedAt;

    @Column
    private Boolean isDuplicate = false;

    @Column
    private UUID duplicateMainOccurrenceId;

    public OccurrenceJpaEntity() {
    }

    public OccurrenceJpaEntity(
        UUID id, UUID userId, UUID categoryId, UUID subCategoryId, String protocolId,
        String description, String additionalNotes, String neighborhood, String referencePoint,
        BigDecimal latitude, BigDecimal longitude, Integer severity, Integer frequency,
        BigDecimal priorityScore, Integer recurrenceCount, Boolean hasPhoto, Integer photoCount,
        String status, BigDecimal confidenceLevel, LocalDateTime createdAt, LocalDateTime updatedAt,
        LocalDateTime resolvedAt, Boolean isDuplicate, UUID duplicateMainOccurrenceId
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
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

    public void setProtocolId(String protocolId) {
        this.protocolId = protocolId;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
