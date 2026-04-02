package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.SpatialCluster;
import opus.social.app.reporteai.domain.port.SpatialClusterRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.SpatialClusterJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.SpatialClusterJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class SpatialClusterRepositoryAdapter implements SpatialClusterRepositoryPort {
    private final SpatialClusterJpaRepository repository;

    public SpatialClusterRepositoryAdapter(SpatialClusterJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public SpatialCluster save(SpatialCluster cluster) {
        return toCluster(repository.save(toEntity(cluster)));
    }

    @Override
    public Optional<SpatialCluster> findById(UUID id) {
        return repository.findById(id).map(this::toCluster);
    }

    @Override
    public List<SpatialCluster> findByNeighborhood(String neighborhood) {
        return repository.findByNeighborhood(neighborhood).stream()
            .map(this::toCluster).toList();
    }

    @Override
    public List<SpatialCluster> findAll() {
        return repository.findAll().stream().map(this::toCluster).toList();
    }

    @Override
    public SpatialCluster update(SpatialCluster cluster) {
        return save(cluster);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private SpatialCluster toCluster(SpatialClusterJpaEntity entity) {
        return new SpatialCluster(entity.getId(), entity.getClusterName(),
            entity.getNeighborhood(), entity.getCenterLatitude(), entity.getCenterLongitude(),
            entity.getRadiusMeters(), entity.getOccurrenceCount(), entity.getDensityScore(),
            entity.getSeverityAvg(), entity.getPriorityScoreAvg(),
            entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private SpatialClusterJpaEntity toEntity(SpatialCluster cluster) {
        return new SpatialClusterJpaEntity(cluster.getId(), cluster.getClusterName(),
            cluster.getNeighborhood(), cluster.getCenterLatitude(),
            cluster.getCenterLongitude(), cluster.getRadiusMeters(),
            cluster.getOccurrenceCount(), cluster.getDensityScore(),
            cluster.getSeverityAvg(), cluster.getPriorityScoreAvg(),
            cluster.getCreatedAt(), cluster.getUpdatedAt());
    }
}
