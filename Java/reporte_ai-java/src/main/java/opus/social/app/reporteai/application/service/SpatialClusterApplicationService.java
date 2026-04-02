package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.SpatialCluster;
import opus.social.app.reporteai.domain.port.SpatialClusterRepositoryPort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SpatialClusterApplicationService {
    private final SpatialClusterRepositoryPort clusterRepository;

    public SpatialClusterApplicationService(SpatialClusterRepositoryPort clusterRepository) {
        this.clusterRepository = clusterRepository;
    }

    public SpatialCluster createCluster(String name, String neighborhood, BigDecimal centerLat,
            BigDecimal centerLon, BigDecimal radius) {
        SpatialCluster cluster = new SpatialCluster(UUID.randomUUID(), name, neighborhood,
            centerLat, centerLon, radius, 0, BigDecimal.ZERO, BigDecimal.ZERO,
            BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now());
        return clusterRepository.save(cluster);
    }

    public List<SpatialCluster> getClustersByNeighborhood(String neighborhood) {
        return clusterRepository.findByNeighborhood(neighborhood);
    }

    public void deleteCluster(UUID id) {
        clusterRepository.delete(id);
    }
}
