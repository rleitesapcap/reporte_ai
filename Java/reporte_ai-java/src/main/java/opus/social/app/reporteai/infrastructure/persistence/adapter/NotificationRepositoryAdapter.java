package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.Notification;
import opus.social.app.reporteai.domain.port.NotificationRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.NotificationJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.NotificationJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {
    private final NotificationJpaRepository repository;

    public NotificationRepositoryAdapter(NotificationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification save(Notification notification) {
        return toNotification(repository.save(toEntity(notification)));
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return repository.findById(id).map(this::toNotification);
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
            .map(this::toNotification).toList();
    }

    @Override
    public List<Notification> findByUserIdAndUnread(UUID userId) {
        return repository.findByUserIdUnread(userId).stream()
            .map(this::toNotification).toList();
    }

    @Override
    public List<Notification> findByType(String type) {
        return repository.findByNotificationType(type).stream()
            .map(this::toNotification).toList();
    }

    @Override
    public Notification update(Notification notification) {
        return save(notification);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private Notification toNotification(NotificationJpaEntity entity) {
        return new Notification(entity.getId(), entity.getUserId(), entity.getOccurrenceId(),
            entity.getNotificationType(), entity.getTitle(), entity.getMessage(),
            entity.getSentAt(), entity.getReadAt(), entity.getIsRead());
    }

    private NotificationJpaEntity toEntity(Notification notification) {
        return new NotificationJpaEntity(notification.getId(), notification.getUserId(),
            notification.getOccurrenceId(), notification.getNotificationType(),
            notification.getTitle(), notification.getMessage(), notification.getSentAt(),
            notification.getReadAt(), notification.getIsRead());
    }
}
