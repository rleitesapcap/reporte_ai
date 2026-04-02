package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.Occurrence;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface OccurrenceRepositoryPort {
    Occurrence save(Occurrence occurrence);
    Optional<Occurrence> findById(UUID id);
    Optional<Occurrence> findByProtocolId(String protocolId);
    List<Occurrence> findByUserId(UUID userId);
    List<Occurrence> findByCategoryId(UUID categoryId);
    List<Occurrence> findByNeighborhood(String neighborhood);
    List<Occurrence> findByStatus(String status);
    List<Occurrence> findByStatusOrderByPriority(String status);
    List<Occurrence> findRecent(int limit);
    List<Occurrence> findAll();
    Occurrence update(Occurrence occurrence);
    void delete(UUID id);
    List<Occurrence> findDuplicates(UUID occurrenceId);
    long countByStatus(String status);
    long countByCategory(UUID categoryId);
}
