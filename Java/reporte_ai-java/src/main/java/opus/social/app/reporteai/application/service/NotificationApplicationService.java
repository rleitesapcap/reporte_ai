package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.Notification;
import opus.social.app.reporteai.domain.port.NotificationRepositoryPort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationApplicationService {
    private final NotificationRepositoryPort notificationRepository;

    public NotificationApplicationService(NotificationRepositoryPort notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification sendNotification(UUID userId, String type, String title, String message) {
        Notification notification = new Notification(UUID.randomUUID(), userId, null, type,
            title, message, LocalDateTime.now(), null, false);
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getUserUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndUnread(userId);
    }

    public Notification markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        return notificationRepository.update(notification);
    }

    public void deleteNotification(UUID id) {
        notificationRepository.delete(id);
    }
}
