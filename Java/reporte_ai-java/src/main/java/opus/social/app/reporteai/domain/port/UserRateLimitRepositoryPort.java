package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.UserRateLimit;
import java.util.Optional;
import java.util.UUID;

public interface UserRateLimitRepositoryPort {
    UserRateLimit save(UserRateLimit rateLimit);
    Optional<UserRateLimit> findByUserId(UUID userId);
    UserRateLimit update(UserRateLimit rateLimit);
    void delete(UUID id);
}
