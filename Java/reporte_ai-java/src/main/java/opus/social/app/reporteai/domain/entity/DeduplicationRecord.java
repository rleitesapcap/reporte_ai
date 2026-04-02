package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class DeduplicationRecord {
    private final UUID id;
    private final UUID mainOccurrenceId;
    private final UUID duplicateOccurrenceId;
    private BigDecimal similarityScore;
    private BigDecimal geographicDistanceMeters;
    private Integer timeDifferenceMinutes;
    private String dedupReason;
    private String dedupMethod;
    private LocalDateTime createdAt;

    public DeduplicationRecord(
        UUID id,
        UUID mainOccurrenceId,
        UUID duplicateOccurrenceId,
        BigDecimal similarityScore,
        BigDecimal geographicDistanceMeters,
        Integer timeDifferenceMinutes,
        String dedupReason,
        String dedupMethod,
        LocalDateTime createdAt
    ) {
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

    public UUID getMainOccurrenceId() {
        return mainOccurrenceId;
    }

    public UUID getDuplicateOccurrenceId() {
        return duplicateOccurrenceId;
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
