package opus.social.app.reporteai.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Report {
    private final UUID id;
    private String title;
    private String reportType;
    private String description;
    private String filePath;
    private Integer fileSize;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private UUID generatedByUserId;
    private LocalDateTime generatedAt;
    private String status;

    public Report(
        UUID id,
        String title,
        String reportType,
        String description,
        String filePath,
        Integer fileSize,
        LocalDate periodStart,
        LocalDate periodEnd,
        UUID generatedByUserId,
        LocalDateTime generatedAt,
        String status
    ) {
        this.id = id;
        this.title = title;
        this.reportType = reportType;
        this.description = description;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.generatedByUserId = generatedByUserId;
        this.generatedAt = generatedAt;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public UUID getGeneratedByUserId() {
        return generatedByUserId;
    }

    public void setGeneratedByUserId(UUID generatedByUserId) {
        this.generatedByUserId = generatedByUserId;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
