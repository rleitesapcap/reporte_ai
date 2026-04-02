package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.NotificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {
    List<NotificationJpaEntity> findByUserId(UUID userId);
    
    @Query("SELECT n FROM NotificationJpaEntity n WHERE n.userId = :userId AND n.isRead = false")
    List<NotificationJpaEntity> findByUserIdUnread(@Param("userId") UUID userId);
    
    List<NotificationJpaEntity> findByNotificationType(String type);
}
