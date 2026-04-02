package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class OccurrenceBuilder {
    private UUID id;
    private UUID userId;
    private UUID categoryId;
    private UUID subCategoryId;
    private String protocolId;
    private String description;
    private String additionalNotes;
    private String neighborhood;
    private String referencePoint;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer severity = 1;
    private Integer frequency = 1;
    private BigDecimal priorityScore;
    private Integer recurrenceCount = 0;
    private Boolean hasPhoto = false;
    private Integer photoCount = 0;
    private String status = "received";
    private BigDecimal confidenceLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private Boolean isDuplicate = false;
    private UUID duplicateMainOccurrenceId;

    public OccurrenceBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public OccurrenceBuilder userId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public OccurrenceBuilder categoryId(UUID categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public OccurrenceBuilder subCategoryId(UUID subCategoryId) {
        this.subCategoryId = subCategoryId;
        return this;
    }

    public OccurrenceBuilder protocolId(String protocolId) {
        this.protocolId = protocolId;
        return this;
    }

    public OccurrenceBuilder description(String description) {
        this.description = description;
        return this;
    }

    public OccurrenceBuilder additionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
        return this;
    }

    public OccurrenceBuilder neighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
        return this;
    }

    public OccurrenceBuilder referencePoint(String referencePoint) {
        this.referencePoint = referencePoint;
        return this;
    }

    public OccurrenceBuilder latitude(BigDecimal latitude) {
        this.latitude = latitude;
        return this;
    }

    public OccurrenceBuilder longitude(BigDecimal longitude) {
        this.longitude = longitude;
        return this;
    }

    public OccurrenceBuilder severity(Integer severity) {
        this.severity = severity;
        return this;
    }

    public OccurrenceBuilder frequency(Integer frequency) {
        this.frequency = frequency;
        return this;
    }

    public OccurrenceBuilder priorityScore(BigDecimal priorityScore) {
        this.priorityScore = priorityScore;
        return this;
    }

    public OccurrenceBuilder recurrenceCount(Integer recurrenceCount) {
        this.recurrenceCount = recurrenceCount;
        return this;
    }

    public OccurrenceBuilder hasPhoto(Boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
        return this;
    }

    public OccurrenceBuilder photoCount(Integer photoCount) {
        this.photoCount = photoCount;
        return this;
    }

    public OccurrenceBuilder status(String status) {
        this.status = status;
        return this;
    }

    public OccurrenceBuilder confidenceLevel(BigDecimal confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
        return this;
    }

    public OccurrenceBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public OccurrenceBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public OccurrenceBuilder resolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
        return this;
    }

    public OccurrenceBuilder isDuplicate(Boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
        return this;
    }

    public OccurrenceBuilder duplicateMainOccurrenceId(UUID duplicateMainOccurrenceId) {
        this.duplicateMainOccurrenceId = duplicateMainOccurrenceId;
        return this;
    }

    public Occurrence build() {
        validateRequired();
        return new Occurrence(
            id,
            userId,
            categoryId,
            subCategoryId,
            protocolId,
            description,
            additionalNotes,
            neighborhood,
            referencePoint,
            latitude,
            longitude,
            severity,
            frequency,
            priorityScore,
            recurrenceCount,
            hasPhoto,
            photoCount,
            status,
            confidenceLevel,
            createdAt,
            updatedAt,
            resolvedAt,
            isDuplicate,
            duplicateMainOccurrenceId
        );
    }

    private void validateRequired() {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID is required");
        }
        if (protocolId == null || protocolId.isBlank()) {
            throw new IllegalArgumentException("Protocol ID is required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (severity == null || severity < 1 || severity > 5) {
            throw new IllegalArgumentException("Severity must be between 1 and 5");
        }
        if (frequency == null || frequency < 1 || frequency > 5) {
            throw new IllegalArgumentException("Frequency must be between 1 and 5");
        }
    }
}
