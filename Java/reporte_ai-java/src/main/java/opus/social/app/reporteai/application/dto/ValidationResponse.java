package opus.social.app.reporteai.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class ValidationResponse {
    private UUID id;
    private UUID occurrenceId;
    private String validationType;
    private String result;
    private String reason;
    private BigDecimal confidence;
    private LocalDateTime validatedAt;

    public ValidationResponse() {}

    public ValidationResponse(UUID id, UUID occurrenceId, String validationType, String result,
            String reason, BigDecimal confidence, LocalDateTime validatedAt) {
        this.id = id;
        this.occurrenceId = occurrenceId;
        this.validationType = validationType;
        this.result = result;
        this.reason = reason;
        this.confidence = confidence;
        this.validatedAt = validatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOccurrenceId() { return occurrenceId; }
    public void setOccurrenceId(UUID occurrenceId) { this.occurrenceId = occurrenceId; }

    public String getValidationType() { return validationType; }
    public void setValidationType(String validationType) { this.validationType = validationType; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
}
