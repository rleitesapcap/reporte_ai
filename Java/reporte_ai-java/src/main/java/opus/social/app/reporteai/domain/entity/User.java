package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class User {
    private final UUID id;
    private final String phoneNumber;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastInteraction;
    private BigDecimal trustScore;
    private Integer totalOccurrences;
    private Integer occurrencesWithPhoto;
    private Boolean isActive;
    private Boolean anonymized;
    private LocalDateTime anonymizedAt;

    public User(
        UUID id,
        String phoneNumber,
        String name,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastInteraction,
        BigDecimal trustScore,
        Integer totalOccurrences,
        Integer occurrencesWithPhoto,
        Boolean isActive,
        Boolean anonymized,
        LocalDateTime anonymizedAt
    ) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastInteraction = lastInteraction;
        this.trustScore = trustScore;
        this.totalOccurrences = totalOccurrences;
        this.occurrencesWithPhoto = occurrencesWithPhoto;
        this.isActive = isActive;
        this.anonymized = anonymized;
        this.anonymizedAt = anonymizedAt;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UUID getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastInteraction() {
        return lastInteraction;
    }

    public void setLastInteraction(LocalDateTime lastInteraction) {
        this.lastInteraction = lastInteraction;
    }

    public BigDecimal getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(BigDecimal trustScore) {
        this.trustScore = trustScore;
    }

    public Integer getTotalOccurrences() {
        return totalOccurrences;
    }

    public void setTotalOccurrences(Integer totalOccurrences) {
        this.totalOccurrences = totalOccurrences;
    }

    public Integer getOccurrencesWithPhoto() {
        return occurrencesWithPhoto;
    }

    public void setOccurrencesWithPhoto(Integer occurrencesWithPhoto) {
        this.occurrencesWithPhoto = occurrencesWithPhoto;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getAnonymized() {
        return anonymized;
    }

    public void setAnonymized(Boolean anonymized) {
        this.anonymized = anonymized;
    }

    public LocalDateTime getAnonymizedAt() {
        return anonymizedAt;
    }

    public void setAnonymizedAt(LocalDateTime anonymizedAt) {
        this.anonymizedAt = anonymizedAt;
    }
}
