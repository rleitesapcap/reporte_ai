package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.Occurrence;
import opus.social.app.reporteai.domain.port.OccurrenceRepositoryPort;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OccurrenceApplicationService {
    private final OccurrenceRepositoryPort occurrenceRepository;

    public OccurrenceApplicationService(OccurrenceRepositoryPort occurrenceRepository) {
        this.occurrenceRepository = occurrenceRepository;
    }

    public Occurrence createOccurrence(UUID userId, UUID categoryId, UUID subCategoryId,
            String protocolId, String description, String neighborhood, String referencePoint,
            BigDecimal latitude, BigDecimal longitude, Integer severity, Integer frequency) {
        
        Occurrence occurrence = Occurrence.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .categoryId(categoryId)
            .subCategoryId(subCategoryId)
            .protocolId(protocolId)
            .description(description)
            .neighborhood(neighborhood)
            .referencePoint(referencePoint)
            .latitude(latitude)
            .longitude(longitude)
            .severity(severity)
            .frequency(frequency)
            .status("received")
            .build();

        return occurrenceRepository.save(occurrence);
    }

    public Occurrence getOccurrenceById(UUID id) {
        return occurrenceRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException("Occurrence not found with id: " + id));
    }

    public Occurrence getOccurrenceByProtocolId(String protocolId) {
        return occurrenceRepository.findByProtocolId(protocolId)
            .orElseThrow(() -> new EmployeeNotFoundException("Occurrence not found with protocol: " + protocolId));
    }

    public List<Occurrence> getOccurrencesByUserId(UUID userId) {
        return occurrenceRepository.findByUserId(userId);
    }

    public List<Occurrence> getOccurrencesByCategory(UUID categoryId) {
        return occurrenceRepository.findByCategoryId(categoryId);
    }

    public List<Occurrence> getOccurrencesByNeighborhood(String neighborhood) {
        return occurrenceRepository.findByNeighborhood(neighborhood);
    }

    public List<Occurrence> getOccurrencesByStatus(String status) {
        return occurrenceRepository.findByStatusOrderByPriority(status);
    }

    public List<Occurrence> getRecentOccurrences(int limit) {
        return occurrenceRepository.findRecent(limit);
    }

    public Occurrence updateOccurrenceStatus(UUID id, String newStatus) {
        Occurrence occurrence = getOccurrenceById(id);
        occurrence.setStatus(newStatus);
        return occurrenceRepository.update(occurrence);
    }

    public void deleteOccurrence(UUID id) {
        occurrenceRepository.delete(id);
    }

    public List<Occurrence> findDuplicateOccurrences(UUID occurrenceId) {
        return occurrenceRepository.findDuplicates(occurrenceId);
    }
}
