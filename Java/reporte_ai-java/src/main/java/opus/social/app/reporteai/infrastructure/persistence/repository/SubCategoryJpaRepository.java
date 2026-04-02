package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.SubCategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface SubCategoryJpaRepository extends JpaRepository<SubCategoryJpaEntity, UUID> {
    List<SubCategoryJpaEntity> findByCategoryId(UUID categoryId);
    
    @Query("SELECT s FROM SubCategoryJpaEntity s WHERE s.categoryId = :categoryId AND s.isActive = true")
    List<SubCategoryJpaEntity> findByCategoryIdActive(@Param("categoryId") UUID categoryId);
    
    @Query("SELECT s FROM SubCategoryJpaEntity s WHERE s.isActive = true")
    List<SubCategoryJpaEntity> findAllActive();
}
