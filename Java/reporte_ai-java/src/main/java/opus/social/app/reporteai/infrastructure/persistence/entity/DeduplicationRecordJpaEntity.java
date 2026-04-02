package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(
    name = "deduplication_records",
    indexes = {
        @Index(name = "idx_dedup_main_occurrence", columnList = "main_occurrence_id"),
        @Index(name = "idx_dedup_duplicate_occurrence", columnList = "duplicate_occurrence_id"),
        @Index(name = "idx_dedup_method", columnList = "dedup_method")
    }
)
public class DeduplicationRecordJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID mainOccurrenceId;

    @Column(nullable = false)
    private UUID duplicateOccurrenceId;

    @Column(precision = 5, scale = 2)
    private BigDecimal similarityScore;

    @Column(precision = 10, scale = 2)
    private BigDecimal geographicDistanceMeters;

    @Column
    private Integer timeDifferenceMinutes;

    @Column(length = 255)
    private String dedupReason;

    @Column(length = 50)
    private String dedupMethod;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DeduplicationRecordJpaEntity() {
    }

    public DeduplicationRecordJpaEntity(UUID id, UUID mainOccurrenceId, UUID duplicateOccurrenceId, BigDecimal similarityScore, BigDecimal geographicDistanceMeters, Integer timeDifferenceMinutes, String dedupReason, String dedupMethod, LocalDateTime createdAt) {
        this.id = id;
        this.mainOccurrenceId = mainOccurrenceId;
        this.duplicateOccurrenceId = duplicateOccurrenceId;
        this.similarityScore = similarityScore;
        this.geographicDistanceMeters = geographicDistanceMeters;
        this.timeDifferenceMinutes = timeDifferenceMinutes;
        this.dedupReason = dedupReason;
        this.dedupMethod = dedupMethod;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMainOccurrenceId() {
        return mainOccurrenceId;
    }

    public void setMainOccurrenceId(UUID mainOccurrenceId) {
        this.mainOccurrenceId = mainOccurrenceId;
    }

    public UUID getDuplicateOccurrenceId() {
        return duplicateOccurrenceId;
    }

    public void setDuplicateOccurrenceId(UUID duplicateOccurrenceId) {
        this.duplicateOccurrenceId = duplicateOccurrenceId;
    }

    public BigDecimal getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(BigDecimal similarityScore) {
        this.similarityScore = similarityScore;
    }

    public BigDecimal getGeographicDistanceMeters() {
        return geographicDistanceMeters;
    }

    public void setGeographicDistanceMeters(BigDecimal geographicDistanceMeters) {
        this.geographicDistanceMeters = geographicDistanceMeters;
    }

    public Integer getTimeDifferenceMinutes() {
        return timeDifferenceMinutes;
    }

    public void setTimeDifferenceMinutes(Integer timeDifferenceMinutes) {
        this.timeDifferenceMinutes = timeDifferenceMinutes;
    }

    public String getDedupReason() {
        return dedupReason;
    }

    public void setDedupReason(String dedupReason) {
        this.dedupReason = dedupReason;
    }

    public String getDedupMethod() {
        return dedupMethod;
    }

    public void setDedupMethod(String dedupMethod) {
        this.dedupMethod = dedupMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
