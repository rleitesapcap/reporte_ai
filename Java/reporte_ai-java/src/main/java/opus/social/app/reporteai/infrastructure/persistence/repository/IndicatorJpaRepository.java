package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.IndicatorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface IndicatorJpaRepository extends JpaRepository<IndicatorJpaEntity, UUID> {
    List<IndicatorJpaEntity> findByIndicatorType(String type);
    List<IndicatorJpaEntity> findByCategoryId(UUID categoryId);
    List<IndicatorJpaEntity> findByNeighborhood(String neighborhood);
}
