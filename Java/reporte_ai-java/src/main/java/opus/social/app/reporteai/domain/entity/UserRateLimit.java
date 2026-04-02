package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;

public class UserRateLimit {
    private final UUID id;
    private final UUID userId;
    private Integer dailyLimit;
    private Integer hourlyLimit;
    private Integer occurrencesToday;
    private Integer occurrencesThisHour;
    private LocalDate lastResetDate;
    private Integer lastResetHour;
    private Boolean isBlocked;
    private LocalDateTime blockedUntil;
    private LocalDateTime updatedAt;

    public UserRateLimit(
        UUID id,
        UUID userId,
        Integer dailyLimit,
        Integer hourlyLimit,
        Integer occurrencesToday,
        Integer occurrencesThisHour,
        LocalDate lastResetDate,
        Integer lastResetHour,
        Boolean isBlocked,
        LocalDateTime blockedUntil,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.dailyLimit = dailyLimit;
        this.hourlyLimit = hourlyLimit;
        this.occurrencesToday = occurrencesToday;
        this.occurrencesThisHour = occurrencesThisHour;
        this.lastResetDate = lastResetDate;
        this.lastResetHour = lastResetHour;
        this.isBlocked = isBlocked;
        this.blockedUntil = blockedUntil;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public Integer getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(Integer dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public Integer getHourlyLimit() {
        return hourlyLimit;
    }

    public void setHourlyLimit(Integer hourlyLimit) {
        this.hourlyLimit = hourlyLimit;
    }

    public Integer getOccurrencesToday() {
        return occurrencesToday;
    }

    public void setOccurrencesToday(Integer occurrencesToday) {
        this.occurrencesToday = occurrencesToday;
    }

    public Integer getOccurrencesThisHour() {
        return occurrencesThisHour;
    }

    public void setOccurrencesThisHour(Integer occurrencesThisHour) {
        this.occurrencesThisHour = occurrencesThisHour;
    }

    public LocalDate getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(LocalDate lastResetDate) {
        this.lastResetDate = lastResetDate;
    }

    public Integer getLastResetHour() {
        return lastResetHour;
    }

    public void setLastResetHour(Integer lastResetHour) {
        this.lastResetHour = lastResetHour;
    }

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(LocalDateTime blockedUntil) {
        this.blockedUntil = blockedUntil;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
