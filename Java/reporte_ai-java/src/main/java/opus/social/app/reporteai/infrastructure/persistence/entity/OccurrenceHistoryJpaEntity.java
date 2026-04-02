package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(
    name = "occurrence_history",
    indexes = {
        @Index(name = "idx_occurrence_history_occurrence_id", columnList = "occurrence_id"),
        @Index(name = "idx_occurrence_history_created_at", columnList = "created_at"),
        @Index(name = "idx_occurrence_history_action", columnList = "action")
    }
)
public class OccurrenceHistoryJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID occurrenceId;

    @Column
    private UUID changedByUserId;

    @Column(length = 50)
    private String action;

    @Column(length = 50)
    private String oldStatus;

    @Column(length = 50)
    private String newStatus;

    @Column(precision = 8, scale = 2)
    private BigDecimal oldPriorityScore;

    @Column(precision = 8, scale = 2)
    private BigDecimal newPriorityScore;

    @Column(columnDefinition = "TEXT")
    private String changeReason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public OccurrenceHistoryJpaEntity() {
    }

    public OccurrenceHistoryJpaEntity(UUID id, UUID occurrenceId, UUID changedByUserId, String action, String oldStatus, String newStatus, BigDecimal oldPriorityScore, BigDecimal newPriorityScore, String changeReason, LocalDateTime createdAt) {
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

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOccurrenceId() {
        return occurrenceId;
    }

    public void setOccurrenceId(UUID occurrenceId) {
        this.occurrenceId = occurrenceId;
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
