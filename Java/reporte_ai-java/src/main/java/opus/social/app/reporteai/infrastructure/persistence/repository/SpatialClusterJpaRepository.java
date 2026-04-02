package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.SpatialClusterJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpatialClusterJpaRepository extends JpaRepository<SpatialClusterJpaEntity, UUID> {
    List<SpatialClusterJpaEntity> findByNeighborhood(String neighborhood);
}
