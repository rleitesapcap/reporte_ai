package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "reports",
    indexes = {
        @Index(name = "idx_reports_report_type", columnList = "report_type"),
        @Index(name = "idx_reports_generated_at", columnList = "generated_at"),
        @Index(name = "idx_reports_period", columnList = "period_start, period_end"),
        @Index(name = "idx_reports_status", columnList = "status")
    }
)
public class ReportJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 50)
    private String reportType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String filePath;

    @Column
    private Integer fileSize;

    @Column
    private LocalDate periodStart;

    @Column
    private LocalDate periodEnd;

    @Column
    private UUID generatedByUserId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @Column(length = 50)
    private String status = "pending";

    public ReportJpaEntity() {
    }

    public ReportJpaEntity(UUID id, String title, String reportType, String description, String filePath, Integer fileSize, LocalDate periodStart, LocalDate periodEnd, UUID generatedByUserId, LocalDateTime generatedAt, String status) {
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

    public void setId(UUID id) {
        this.id = id;
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
