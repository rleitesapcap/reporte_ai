package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.UserConsent;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface UserConsentRepositoryPort {
    UserConsent save(UserConsent consent);
    Optional<UserConsent> findById(UUID id);
    List<UserConsent> findByUserId(UUID userId);
    List<UserConsent> findByUserIdAndType(UUID userId, String consentType);
    UserConsent update(UserConsent consent);
    void delete(UUID id);
}
