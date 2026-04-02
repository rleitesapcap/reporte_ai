package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class Validation {
    private final UUID id;
    private final UUID occurrenceId;
    private UUID validatorUserId;
    private String validationType;
    private String result;
    private String reason;
    private BigDecimal confidence;
    private Integer multipleReportsCount;
    private LocalDateTime validatedAt;

    public Validation(
        UUID id,
        UUID occurrenceId,
        UUID validatorUserId,
        String validationType,
        String result,
        String reason,
        BigDecimal confidence,
        Integer multipleReportsCount,
        LocalDateTime validatedAt
    ) {
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

    public UUID getOccurrenceId() {
        return occurrenceId;
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
