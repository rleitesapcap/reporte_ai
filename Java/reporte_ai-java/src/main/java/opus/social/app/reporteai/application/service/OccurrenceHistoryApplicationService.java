package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.OccurrenceHistory;
import opus.social.app.reporteai.domain.port.OccurrenceHistoryRepositoryPort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OccurrenceHistoryApplicationService {
    private final OccurrenceHistoryRepositoryPort historyRepository;

    public OccurrenceHistoryApplicationService(OccurrenceHistoryRepositoryPort historyRepository) {
        this.historyRepository = historyRepository;
    }

    public OccurrenceHistory recordChange(UUID occurrenceId, String action, String oldStatus,
            String newStatus, BigDecimal oldScore, BigDecimal newScore, String reason) {
        OccurrenceHistory history = new OccurrenceHistory(UUID.randomUUID(), occurrenceId,
            null, action, oldStatus, newStatus, oldScore, newScore, reason, LocalDateTime.now());
        return historyRepository.save(history);
    }

    public List<OccurrenceHistory> getOccurrenceHistory(UUID occurrenceId) {
        return historyRepository.findByOccurrenceId(occurrenceId);
    }

    public void deleteHistory(UUID id) {
        historyRepository.delete(id);
    }
}
