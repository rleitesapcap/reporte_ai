package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.OccurrenceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface OccurrenceJpaRepository extends JpaRepository<OccurrenceJpaEntity, UUID> {
    Optional<OccurrenceJpaEntity> findByProtocolId(String protocolId);
    
    List<OccurrenceJpaEntity> findByUserId(UUID userId);
    
    List<OccurrenceJpaEntity> findByCategoryId(UUID categoryId);
    
    List<OccurrenceJpaEntity> findByNeighborhood(String neighborhood);
    
    List<OccurrenceJpaEntity> findByStatus(String status);
    
    @Query("SELECT o FROM OccurrenceJpaEntity o WHERE o.status = :status ORDER BY o.priorityScore DESC")
    List<OccurrenceJpaEntity> findByStatusOrderByPriority(@Param("status") String status);
    
    @Query(value = "SELECT * FROM occurrences WHERE is_duplicate = false ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<OccurrenceJpaEntity> findRecent(@Param("limit") int limit);
    
    @Query("SELECT o FROM OccurrenceJpaEntity o WHERE o.duplicateMainOccurrenceId = :occurrenceId OR o.id IN (SELECT d.duplicateOccurrenceId FROM DeduplicationRecordJpaEntity d WHERE d.mainOccurrenceId = :occurrenceId)")
    List<OccurrenceJpaEntity> findDuplicates(@Param("occurrenceId") UUID occurrenceId);
    
    long countByStatus(String status);
    
    long countByCategoryId(UUID categoryId);
}
