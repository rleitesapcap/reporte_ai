package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "user_rate_limit",
    indexes = {
        @Index(name = "idx_user_rate_limit_user_id", columnList = "user_id"),
        @Index(name = "idx_user_rate_limit_is_blocked", columnList = "is_blocked")
    }
)
public class UserRateLimitJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column
    private Integer dailyLimit = 10;

    @Column
    private Integer hourlyLimit = 3;

    @Column
    private Integer occurrencesToday = 0;

    @Column
    private Integer occurrencesThisHour = 0;

    @Column
    private LocalDate lastResetDate;

    @Column
    private Integer lastResetHour;

    @Column
    private Boolean isBlocked = false;

    @Column
    private LocalDateTime blockedUntil;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public UserRateLimitJpaEntity() {
    }

    public UserRateLimitJpaEntity(UUID id, UUID userId, Integer dailyLimit, Integer hourlyLimit, Integer occurrencesToday, Integer occurrencesThisHour, LocalDate lastResetDate, Integer lastResetHour, Boolean isBlocked, LocalDateTime blockedUntil, LocalDateTime updatedAt) {
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

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
