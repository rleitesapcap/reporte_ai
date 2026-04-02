package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class OccurrenceHistory {
    private final UUID id;
    private final UUID occurrenceId;
    private UUID changedByUserId;
    private String action;
    private String oldStatus;
    private String newStatus;
    private BigDecimal oldPriorityScore;
    private BigDecimal newPriorityScore;
    private String changeReason;
    private LocalDateTime createdAt;

    public OccurrenceHistory(
        UUID id,
        UUID occurrenceId,
        UUID changedByUserId,
        String action,
        String oldStatus,
        String newStatus,
        BigDecimal oldPriorityScore,
        BigDecimal newPriorityScore,
        String changeReason,
        LocalDateTime createdAt
    ) {
        this.id = id;
        this.occurrenceId = occurrenceId;
        this.changedByUserId = changedByUserId;
        this.action = action;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.oldPriorityScore = oldPriorityScore;
        this.newPriorityScore = newPriorityScore;
        this.changeReason = changeReason;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOccurrenceId() {
        return occurrenceId;
    }

    public UUID getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(UUID changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public BigDecimal getOldPriorityScore() {
        return oldPriorityScore;
    }

    public void setOldPriorityScore(BigDecimal oldPriorityScore) {
        this.oldPriorityScore = oldPriorityScore;
    }

    public BigDecimal getNewPriorityScore() {
        return newPriorityScore;
    }

    public void setNewPriorityScore(BigDecimal newPriorityScore) {
        this.newPriorityScore = newPriorityScore;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
