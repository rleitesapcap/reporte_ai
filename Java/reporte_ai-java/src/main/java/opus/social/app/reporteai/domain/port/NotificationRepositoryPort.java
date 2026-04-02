package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.Notification;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
    Optional<Notification> findById(UUID id);
    List<Notification> findByUserId(UUID userId);
    List<Notification> findByUserIdAndUnread(UUID userId);
    List<Notification> findByType(String type);
    Notification update(Notification notification);
    void delete(UUID id);
}
