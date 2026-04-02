package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.ReportJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReportJpaRepository extends JpaRepository<ReportJpaEntity, UUID> {
    List<ReportJpaEntity> findByReportType(String type);
    List<ReportJpaEntity> findByStatus(String status);
    List<ReportJpaEntity> findByGeneratedByUserId(UUID userId);
}
