package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.DeduplicationRecord;
import opus.social.app.reporteai.domain.port.DeduplicationRecordRepositoryPort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DeduplicationRecordApplicationService {
    private final DeduplicationRecordRepositoryPort dedupRepository;

    public DeduplicationRecordApplicationService(DeduplicationRecordRepositoryPort dedupRepository) {
        this.dedupRepository = dedupRepository;
    }

    public DeduplicationRecord recordDuplicate(UUID mainOccurrenceId, UUID duplicateId,
            BigDecimal similarity, BigDecimal distance, Integer timeDiff, String method) {
        DeduplicationRecord record = new DeduplicationRecord(UUID.randomUUID(), mainOccurrenceId,
            duplicateId, similarity, distance, timeDiff, null, method, LocalDateTime.now());
        return dedupRepository.save(record);
    }

    public List<DeduplicationRecord> getDuplicatesForOccurrence(UUID occurrenceId) {
        return dedupRepository.findByMainOccurrenceId(occurrenceId);
    }

    public void deleteRecord(UUID id) {
        dedupRepository.delete(id);
    }
}
