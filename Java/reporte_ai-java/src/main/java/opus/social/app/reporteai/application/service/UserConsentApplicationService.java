package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.UserConsent;
import opus.social.app.reporteai.domain.port.UserConsentRepositoryPort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserConsentApplicationService {
    private final UserConsentRepositoryPort consentRepository;

    public UserConsentApplicationService(UserConsentRepositoryPort consentRepository) {
        this.consentRepository = consentRepository;
    }

    public UserConsent recordConsent(UUID userId, String consentType, Boolean accepted,
            String documentVersion, String ipAddress) {
        UserConsent consent = new UserConsent(UUID.randomUUID(), userId, consentType,
            accepted, LocalDateTime.now(), documentVersion, ipAddress);
        return consentRepository.save(consent);
    }

    public List<UserConsent> getUserConsents(UUID userId) {
        return consentRepository.findByUserId(userId);
    }

    public List<UserConsent> getUserConsentsByType(UUID userId, String consentType) {
        return consentRepository.findByUserIdAndType(userId, consentType);
    }

    public void deleteConsent(UUID id) {
        consentRepository.delete(id);
    }
}
