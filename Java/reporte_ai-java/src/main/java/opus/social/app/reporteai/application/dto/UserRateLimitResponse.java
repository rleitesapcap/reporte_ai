package opus.social.app.reporteai.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserRateLimitResponse {
    private UUID id;
    private UUID userId;
    private Integer dailyLimit;
    private Integer hourlyLimit;
    private Integer occurrencesToday;
    private Integer occurrencesThisHour;
    private Boolean isBlocked;
    private LocalDateTime blockedUntil;
    private LocalDateTime updatedAt;

    public UserRateLimitResponse() {}

    public UserRateLimitResponse(UUID id, UUID userId, Integer dailyLimit, Integer hourlyLimit,
            Integer today, Integer thisHour, Boolean blocked, LocalDateTime until, LocalDateTime updated) {
        this.id = id;
        this.userId = userId;
        this.dailyLimit = dailyLimit;
        this.hourlyLimit = hourlyLimit;
        this.occurrencesToday = today;
        this.occurrencesThisHour = thisHour;
        this.isBlocked = blocked;
        this.blockedUntil = until;
        this.updatedAt = updated;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Integer getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(Integer limit) { this.dailyLimit = limit; }

    public Integer getHourlyLimit() { return hourlyLimit; }
    public void setHourlyLimit(Integer limit) { this.hourlyLimit = limit; }

    public Integer getOccurrencesToday() { return occurrencesToday; }
    public void setOccurrencesToday(Integer today) { this.occurrencesToday = today; }

    public Integer getOccurrencesThisHour() { return occurrencesThisHour; }
    public void setOccurrencesThisHour(Integer hour) { this.occurrencesThisHour = hour; }

    public Boolean getIsBlocked() { return isBlocked; }
    public void setIsBlocked(Boolean blocked) { this.isBlocked = blocked; }

    public LocalDateTime getBlockedUntil() { return blockedUntil; }
    public void setBlockedUntil(LocalDateTime until) { this.blockedUntil = until; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updated) { this.updatedAt = updated; }
}
