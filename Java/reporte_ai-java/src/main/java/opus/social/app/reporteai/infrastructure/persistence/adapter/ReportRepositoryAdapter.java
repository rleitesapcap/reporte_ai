package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.Report;
import opus.social.app.reporteai.domain.port.ReportRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.ReportJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.ReportJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class ReportRepositoryAdapter implements ReportRepositoryPort {
    private final ReportJpaRepository repository;

    public ReportRepositoryAdapter(ReportJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Report save(Report report) {
        return toReport(repository.save(toEntity(report)));
    }

    @Override
    public Optional<Report> findById(UUID id) {
        return repository.findById(id).map(this::toReport);
    }

    @Override
    public List<Report> findByType(String type) {
        return repository.findByReportType(type).stream().map(this::toReport).toList();
    }

    @Override
    public List<Report> findByStatus(String status) {
        return repository.findByStatus(status).stream().map(this::toReport).toList();
    }

    @Override
    public List<Report> findByGeneratedByUserId(UUID userId) {
        return repository.findByGeneratedByUserId(userId).stream()
            .map(this::toReport).toList();
    }

    @Override
    public Report update(Report report) {
        return save(report);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private Report toReport(ReportJpaEntity entity) {
        return new Report(entity.getId(), entity.getTitle(), entity.getReportType(),
            entity.getDescription(), entity.getFilePath(), entity.getFileSize(),
            entity.getPeriodStart(), entity.getPeriodEnd(), entity.getGeneratedByUserId(),
            entity.getGeneratedAt(), entity.getStatus());
    }

    private ReportJpaEntity toEntity(Report report) {
        return new ReportJpaEntity(report.getId(), report.getTitle(), report.getReportType(),
            report.getDescription(), report.getFilePath(), report.getFileSize(),
            report.getPeriodStart(), report.getPeriodEnd(), report.getGeneratedByUserId(),
            report.getGeneratedAt(), report.getStatus());
    }
}
