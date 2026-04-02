package opus.social.app.reporteai.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import java.math.BigDecimal;

public class ValidationCreateRequest {
    @NotNull(message = "Occurrence ID is required")
    private UUID occurrenceId;

    @NotBlank(message = "Validation type is required")
    private String validationType;

    @NotBlank(message = "Result is required")
    private String result;

    private String reason;
    private BigDecimal confidence;
    private Integer multipleReportsCount;

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

    public Integer getMultipleReportsCount() { return multipleReportsCount; }
    public void setMultipleReportsCount(Integer multipleReportsCount) { this.multipleReportsCount = multipleReportsCount; }
}
