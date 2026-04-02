package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(
    name = "validations",
    indexes = {
        @Index(name = "idx_validations_occurrence_id", columnList = "occurrence_id"),
        @Index(name = "idx_validations_validator_user_id", columnList = "validator_user_id"),
        @Index(name = "idx_validations_result", columnList = "result"),
        @Index(name = "idx_validations_validated_at", columnList = "validated_at")
    }
)
public class ValidationJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID occurrenceId;

    @Column
    private UUID validatorUserId;

    @Column(length = 50)
    private String validationType;

    @Column(nullable = false, length = 50)
    private String result;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(precision = 4, scale = 2)
    private BigDecimal confidence;

    @Column
    private Integer multipleReportsCount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime validatedAt;

    public ValidationJpaEntity() {
    }

    public ValidationJpaEntity(UUID id, UUID occurrenceId, UUID validatorUserId, String validationType, String result, String reason, BigDecimal confidence, Integer multipleReportsCount, LocalDateTime validatedAt) {
        this.id = id;
        this.occurrenceId = occurrenceId;
        this.validatorUserId = validatorUserId;
        this.validationType = validationType;
        this.result = result;
        this.reason = reason;
        this.confidence = confidence;
        this.multipleReportsCount = multipleReportsCount;
        this.validatedAt = validatedAt;
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

    public UUID getValidatorUserId() {
        return validatorUserId;
    }

    public void setValidatorUserId(UUID validatorUserId) {
        this.validatorUserId = validatorUserId;
    }

    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public Integer getMultipleReportsCount() {
        return multipleReportsCount;
    }

    public void setMultipleReportsCount(Integer multipleReportsCount) {
        this.multipleReportsCount = multipleReportsCount;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }
}
