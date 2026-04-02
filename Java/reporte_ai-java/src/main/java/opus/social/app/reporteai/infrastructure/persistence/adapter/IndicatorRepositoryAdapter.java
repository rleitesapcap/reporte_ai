package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.Indicator;
import opus.social.app.reporteai.domain.port.IndicatorRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.IndicatorJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.IndicatorJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class IndicatorRepositoryAdapter implements IndicatorRepositoryPort {
    private final IndicatorJpaRepository repository;

    public IndicatorRepositoryAdapter(IndicatorJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Indicator save(Indicator indicator) {
        return toIndicator(repository.save(toEntity(indicator)));
    }

    @Override
    public Optional<Indicator> findById(UUID id) {
        return repository.findById(id).map(this::toIndicator);
    }

    @Override
    public List<Indicator> findByType(String type) {
        return repository.findByIndicatorType(type).stream().map(this::toIndicator).toList();
    }

    @Override
    public List<Indicator> findByCategoryId(UUID categoryId) {
        return repository.findByCategoryId(categoryId).stream().map(this::toIndicator).toList();
    }

    @Override
    public List<Indicator> findByNeighborhood(String neighborhood) {
        return repository.findByNeighborhood(neighborhood).stream()
            .map(this::toIndicator).toList();
    }

    @Override
    public Indicator update(Indicator indicator) {
        return save(indicator);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private Indicator toIndicator(IndicatorJpaEntity entity) {
        return new Indicator(entity.getId(), entity.getIndicatorName(),
            entity.getIndicatorType(), entity.getDescription(), entity.getValue(),
            entity.getUnit(), entity.getCategoryId(), entity.getNeighborhood(),
            entity.getPeriodStart(), entity.getPeriodEnd(), entity.getCalculatedAt());
    }

    private IndicatorJpaEntity toEntity(Indicator indicator) {
        return new IndicatorJpaEntity(indicator.getId(), indicator.getIndicatorName(),
            indicator.getIndicatorType(), indicator.getDescription(), indicator.getValue(),
            indicator.getUnit(), indicator.getCategoryId(), indicator.getNeighborhood(),
            indicator.getPeriodStart(), indicator.getPeriodEnd(), indicator.getCalculatedAt());
    }
}
