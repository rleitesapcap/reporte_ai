package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.Report;
import opus.social.app.reporteai.domain.port.ReportRepositoryPort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReportApplicationService {
    private final ReportRepositoryPort reportRepository;

    public ReportApplicationService(ReportRepositoryPort reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report createReport(String title, String type, String description,
            LocalDate startDate, LocalDate endDate) {
        Report report = new Report(UUID.randomUUID(), title, type, description, null, null,
            startDate, endDate, null, LocalDateTime.now(), "pending");
        return reportRepository.save(report);
    }

    public List<Report> getReportsByType(String type) {
        return reportRepository.findByType(type);
    }

    public List<Report> getReportsByStatus(String status) {
        return reportRepository.findByStatus(status);
    }

    public Report updateReportStatus(UUID id, String status) {
        Report report = reportRepository.findById(id).orElseThrow();
        report.setStatus(status);
        return reportRepository.update(report);
    }

    public void deleteReport(UUID id) {
        reportRepository.delete(id);
    }
}
