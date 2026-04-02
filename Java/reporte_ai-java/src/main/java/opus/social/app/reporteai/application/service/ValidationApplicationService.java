package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.Validation;
import opus.social.app.reporteai.domain.port.ValidationRepositoryPort;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ValidationApplicationService {
    private final ValidationRepositoryPort validationRepository;

    public ValidationApplicationService(ValidationRepositoryPort validationRepository) {
        this.validationRepository = validationRepository;
    }

    public Validation createValidation(UUID occurrenceId, String validationType, String result,
            String reason, BigDecimal confidence) {
        Validation validation = new Validation(UUID.randomUUID(), occurrenceId, null,
            validationType, result, reason, confidence, null, null);
        return validationRepository.save(validation);
    }

    public Validation getValidationById(UUID id) {
        return validationRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException("Validation not found"));
    }

    public List<Validation> getValidationsByOccurrence(UUID occurrenceId) {
        return validationRepository.findByOccurrenceId(occurrenceId);
    }

    public List<Validation> getValidationsByResult(String result) {
        return validationRepository.findByResult(result);
    }

    public Validation updateValidationResult(UUID id, String result, String reason) {
        Validation validation = getValidationById(id);
        validation.setResult(result);
        validation.setReason(reason);
        return validationRepository.update(validation);
    }

    public void deleteValidation(UUID id) {
        validationRepository.delete(id);
    }
}
