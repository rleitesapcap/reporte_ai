package opus.social.app.reporteai.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class OccurrenceImageCreateRequest {
    @NotNull(message = "Occurrence ID is required")
    private UUID occurrenceId;

    @NotBlank(message = "S3 URL is required")
    private String s3Url;

    @NotBlank(message = "S3 Key is required")
    private String s3Key;

    private Integer imageSize;
    private String imageFormat;

    public UUID getOccurrenceId() { return occurrenceId; }
    public void setOccurrenceId(UUID occurrenceId) { this.occurrenceId = occurrenceId; }

    public String getS3Url() { return s3Url; }
    public void setS3Url(String s3Url) { this.s3Url = s3Url; }

    public String getS3Key() { return s3Key; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }

    public Integer getImageSize() { return imageSize; }
    public void setImageSize(Integer imageSize) { this.imageSize = imageSize; }

    public String getImageFormat() { return imageFormat; }
    public void setImageFormat(String imageFormat) { this.imageFormat = imageFormat; }
}
