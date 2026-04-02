package opus.social.app.reporteai.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class DeduplicationRecordResponse {
    private UUID id;
    private UUID mainOccurrenceId;
    private UUID duplicateOccurrenceId;
    private BigDecimal similarityScore;
    private BigDecimal geographicDistanceMeters;
    private Integer timeDifferenceMinutes;
    private String dedupReason;
    private String dedupMethod;
    private LocalDateTime createdAt;

    public DeduplicationRecordResponse() {}

    public DeduplicationRecordResponse(UUID id, UUID mainId, UUID dupId, BigDecimal similarity,
            BigDecimal distance, Integer timeDiff, String reason, String method, LocalDateTime createdAt) {
        this.id = id;
        this.mainOccurrenceId = mainId;
        this.duplicateOccurrenceId = dupId;
        this.similarityScore = similarity;
        this.geographicDistanceMeters = distance;
        this.timeDifferenceMinutes = timeDiff;
        this.dedupReason = reason;
        this.dedupMethod = method;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getMainOccurrenceId() { return mainOccurrenceId; }
    public void setMainOccurrenceId(UUID id) { this.mainOccurrenceId = id; }

    public UUID getDuplicateOccurrenceId() { return duplicateOccurrenceId; }
    public void setDuplicateOccurrenceId(UUID id) { this.duplicateOccurrenceId = id; }

    public BigDecimal getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(BigDecimal score) { this.similarityScore = score; }

    public BigDecimal getGeographicDistanceMeters() { return geographicDistanceMeters; }
    public void setGeographicDistanceMeters(BigDecimal distance) { this.geographicDistanceMeters = distance; }

    public Integer getTimeDifferenceMinutes() { return timeDifferenceMinutes; }
    public void setTimeDifferenceMinutes(Integer minutes) { this.timeDifferenceMinutes = minutes; }

    public String getDedupReason() { return dedupReason; }
    public void setDedupReason(String reason) { this.dedupReason = reason; }

    public String getDedupMethod() { return dedupMethod; }
    public void setDedupMethod(String method) { this.dedupMethod = method; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
