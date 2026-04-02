package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.Report;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface ReportRepositoryPort {
    Report save(Report report);
    Optional<Report> findById(UUID id);
    List<Report> findByType(String type);
    List<Report> findByStatus(String status);
    List<Report> findByGeneratedByUserId(UUID userId);
    Report update(Report report);
    void delete(UUID id);
}
