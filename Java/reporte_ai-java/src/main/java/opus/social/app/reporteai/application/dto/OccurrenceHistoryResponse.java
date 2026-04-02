package opus.social.app.reporteai.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class OccurrenceHistoryResponse {
    private UUID id;
    private UUID occurrenceId;
    private String action;
    private String oldStatus;
    private String newStatus;
    private BigDecimal oldPriorityScore;
    private BigDecimal newPriorityScore;
    private String changeReason;
    private LocalDateTime createdAt;

    public OccurrenceHistoryResponse() {}

    public OccurrenceHistoryResponse(UUID id, UUID occurrenceId, String action, String oldStatus,
            String newStatus, BigDecimal oldScore, BigDecimal newScore, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.occurrenceId = occurrenceId;
        this.action = action;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.oldPriorityScore = oldScore;
        this.newPriorityScore = newScore;
        this.changeReason = reason;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOccurrenceId() { return occurrenceId; }
    public void setOccurrenceId(UUID occurrenceId) { this.occurrenceId = occurrenceId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public BigDecimal getOldPriorityScore() { return oldPriorityScore; }
    public void setOldPriorityScore(BigDecimal score) { this.oldPriorityScore = score; }

    public BigDecimal getNewPriorityScore() { return newPriorityScore; }
    public void setNewPriorityScore(BigDecimal score) { this.newPriorityScore = score; }

    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
