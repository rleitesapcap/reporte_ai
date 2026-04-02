package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.OccurrenceImage;
import opus.social.app.reporteai.domain.port.OccurrenceImageRepositoryPort;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OccurrenceImageApplicationService {
    private final OccurrenceImageRepositoryPort imageRepository;

    public OccurrenceImageApplicationService(OccurrenceImageRepositoryPort imageRepository) {
        this.imageRepository = imageRepository;
    }

    public OccurrenceImage uploadImage(UUID occurrenceId, String s3Url, String s3Key,
            Integer imageSize, String imageFormat) {
        OccurrenceImage image = new OccurrenceImage(UUID.randomUUID(), occurrenceId, s3Url,
            s3Key, imageSize, imageFormat, LocalDateTime.now(), false);
        return imageRepository.save(image);
    }

    public OccurrenceImage getImageById(UUID id) {
        return imageRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException("Image not found"));
    }

    public List<OccurrenceImage> getImagesByOccurrence(UUID occurrenceId) {
        return imageRepository.findByOccurrenceId(occurrenceId);
    }

    public OccurrenceImage markImageAsProcessed(UUID id) {
        OccurrenceImage image = getImageById(id);
        image.setProcessed(true);
        return imageRepository.update(image);
    }

    public void deleteImage(UUID id) {
        imageRepository.delete(id);
    }

    public void deleteImagesByOccurrence(UUID occurrenceId) {
        imageRepository.deleteByOccurrenceId(occurrenceId);
    }
}
