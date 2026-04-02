package opus.social.app.reporteai.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationResponse {
    private UUID id;
    private UUID userId;
    private UUID occurrenceId;
    private String notificationType;
    private String title;
    private String message;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private Boolean isRead;

    public NotificationResponse() {}

    public NotificationResponse(UUID id, UUID userId, UUID occurrenceId, String type,
            String title, String message, LocalDateTime sent, LocalDateTime read, Boolean read_flag) {
        this.id = id;
        this.userId = userId;
        this.occurrenceId = occurrenceId;
        this.notificationType = type;
        this.title = title;
        this.message = message;
        this.sentAt = sent;
        this.readAt = read;
        this.isRead = read_flag;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getOccurrenceId() { return occurrenceId; }
    public void setOccurrenceId(UUID occurrenceId) { this.occurrenceId = occurrenceId; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String type) { this.notificationType = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sent) { this.sentAt = sent; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime read) { this.readAt = read; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean read_flag) { this.isRead = read_flag; }
}
