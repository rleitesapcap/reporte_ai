package opus.social.app.reporteai.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReportResponse {
    private UUID id;
    private String title;
    private String reportType;
    private String description;
    private String filePath;
    private Integer fileSize;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDateTime generatedAt;
    private String status;

    public ReportResponse() {}

    public ReportResponse(UUID id, String title, String type, String description, String filePath,
            Integer fileSize, LocalDate start, LocalDate end, LocalDateTime generated, String status) {
        this.id = id;
        this.title = title;
        this.reportType = type;
        this.description = description;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.periodStart = start;
        this.periodEnd = end;
        this.generatedAt = generated;
        this.status = status;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getReportType() { return reportType; }
    public void setReportType(String type) { this.reportType = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Integer getFileSize() { return fileSize; }
    public void setFileSize(Integer fileSize) { this.fileSize = fileSize; }

    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate start) { this.periodStart = start; }

    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate end) { this.periodEnd = end; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generated) { this.generatedAt = generated; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
