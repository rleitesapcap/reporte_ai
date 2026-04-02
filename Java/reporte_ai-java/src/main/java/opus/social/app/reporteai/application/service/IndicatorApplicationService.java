package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.Indicator;
import opus.social.app.reporteai.domain.port.IndicatorRepositoryPort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class IndicatorApplicationService {
    private final IndicatorRepositoryPort indicatorRepository;

    public IndicatorApplicationService(IndicatorRepositoryPort indicatorRepository) {
        this.indicatorRepository = indicatorRepository;
    }

    public Indicator createIndicator(String name, String type, BigDecimal value, String unit,
            LocalDate startDate, LocalDate endDate) {
        Indicator indicator = new Indicator(UUID.randomUUID(), name, type, null, value, unit,
            null, null, startDate, endDate, LocalDateTime.now());
        return indicatorRepository.save(indicator);
    }

    public List<Indicator> getIndicatorsByType(String type) {
        return indicatorRepository.findByType(type);
    }

    public List<Indicator> getIndicatorsByCategory(UUID categoryId) {
        return indicatorRepository.findByCategoryId(categoryId);
    }

    public void deleteIndicator(UUID id) {
        indicatorRepository.delete(id);
    }
}
