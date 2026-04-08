package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "occurrence_images",
    indexes = {
        @Index(name = "idx_occurrence_images_occurrence_id", columnList = "occurrence_id"),
        @Index(name = "idx_occurrence_images_uploaded_at", columnList = "uploaded_at")
    }
)
public class OccurrenceImageJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID occurrenceId;

    @Column(name = "s3_url", nullable = false, length = 500)
    private String s3Url;

    @Column(name = "s3_key", nullable = false, unique = true, length = 500)
    private String s3Key;

    @Column
    private Integer imageSize;

    @Column(length = 10)
    private String imageFormat;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @Column
    private Boolean processed = false;

    public OccurrenceImageJpaEntity() {
    }

    public OccurrenceImageJpaEntity(UUID id, UUID occurrenceId, String s3Url, String s3Key, Integer imageSize, String imageFormat, LocalDateTime uploadedAt, Boolean processed) {
        this.id = id;
        this.occurrenceId = occurrenceId;
        this.s3Url = s3Url;
        this.s3Key = s3Key;
        this.imageSize = imageSize;
        this.imageFormat = imageFormat;
        this.uploadedAt = uploadedAt;
        this.processed = processed;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOccurrenceId() {
        return occurrenceId;
    }

    public void setOccurrenceId(UUID occurrenceId) {
        this.occurrenceId = occurrenceId;
    }

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public Integer getImageSize() {
        return imageSize;
    }

    public void setImageSize(Integer imageSize) {
        this.imageSize = imageSize;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }
}
