package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    private final UUID id;
    private final UUID userId;
    private UUID occurrenceId;
    private String notificationType;
    private String title;
    private String message;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private Boolean isRead;

    public Notification(
        UUID id,
        UUID userId,
        UUID occurrenceId,
        String notificationType,
        String title,
        String message,
        LocalDateTime sentAt,
        LocalDateTime readAt,
        Boolean isRead
    ) {
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

    public UUID getUserId() {
        return userId;
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
