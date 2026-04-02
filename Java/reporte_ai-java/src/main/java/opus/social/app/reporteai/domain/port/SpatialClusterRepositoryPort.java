package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.SpatialCluster;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface SpatialClusterRepositoryPort {
    SpatialCluster save(SpatialCluster cluster);
    Optional<SpatialCluster> findById(UUID id);
    List<SpatialCluster> findByNeighborhood(String neighborhood);
    List<SpatialCluster> findAll();
    SpatialCluster update(SpatialCluster cluster);
    void delete(UUID id);
}
