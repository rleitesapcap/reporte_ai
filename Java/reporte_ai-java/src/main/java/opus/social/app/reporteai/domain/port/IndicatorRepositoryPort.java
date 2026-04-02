package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.Indicator;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface IndicatorRepositoryPort {
    Indicator save(Indicator indicator);
    Optional<Indicator> findById(UUID id);
    List<Indicator> findByType(String type);
    List<Indicator> findByCategoryId(UUID categoryId);
    List<Indicator> findByNeighborhood(String neighborhood);
    Indicator update(Indicator indicator);
    void delete(UUID id);
}
