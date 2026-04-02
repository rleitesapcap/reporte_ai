package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notifications_user_id", columnList = "user_id"),
        @Index(name = "idx_notifications_is_read", columnList = "is_read"),
        @Index(name = "idx_notifications_sent_at", columnList = "sent_at")
    }
)
public class NotificationJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column
    private UUID occurrenceId;

    @Column(length = 50)
    private String notificationType;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column
    private LocalDateTime readAt;

    @Column
    private Boolean isRead = false;

    public NotificationJpaEntity() {
    }

    public NotificationJpaEntity(UUID id, UUID userId, UUID occurrenceId, String notificationType, String title, String message, LocalDateTime sentAt, LocalDateTime readAt, Boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.occurrenceId = occurrenceId;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.sentAt = sentAt;
        this.readAt = readAt;
        this.isRead = isRead;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getOccurrenceId() {
        return occurrenceId;
    }

    public void setOccurrenceId(UUID occurrenceId) {
        this.occurrenceId = occurrenceId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
