package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.UserRateLimit;
import opus.social.app.reporteai.domain.port.UserRateLimitRepositoryPort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserRateLimitApplicationService {
    private final UserRateLimitRepositoryPort rateLimitRepository;

    public UserRateLimitApplicationService(UserRateLimitRepositoryPort rateLimitRepository) {
        this.rateLimitRepository = rateLimitRepository;
    }

    public UserRateLimit createRateLimit(UUID userId) {
        UserRateLimit rateLimit = new UserRateLimit(UUID.randomUUID(), userId, 10, 3,
            0, 0, null, null, false, null, LocalDateTime.now());
        return rateLimitRepository.save(rateLimit);
    }

    public UserRateLimit getRateLimitByUserId(UUID userId) {
        return rateLimitRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("RateLimit not found"));
    }

    public void blockUser(UUID userId, LocalDateTime until) {
        UserRateLimit rateLimit = getRateLimitByUserId(userId);
        rateLimit.setIsBlocked(true);
        rateLimit.setBlockedUntil(until);
        rateLimitRepository.update(rateLimit);
    }

    public void unblockUser(UUID userId) {
        UserRateLimit rateLimit = getRateLimitByUserId(userId);
        rateLimit.setIsBlocked(false);
        rateLimitRepository.update(rateLimit);
    }
}
