package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class OccurrenceImage {
    private final UUID id;
    private final UUID occurrenceId;
    private String s3Url;
    private String s3Key;
    private Integer imageSize;
    private String imageFormat;
    private LocalDateTime uploadedAt;
    private Boolean processed;

    public OccurrenceImage(
        UUID id,
        UUID occurrenceId,
        String s3Url,
        String s3Key,
        Integer imageSize,
        String imageFormat,
        LocalDateTime uploadedAt,
        Boolean processed
    ) {
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

    public UUID getOccurrenceId() {
        return occurrenceId;
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
