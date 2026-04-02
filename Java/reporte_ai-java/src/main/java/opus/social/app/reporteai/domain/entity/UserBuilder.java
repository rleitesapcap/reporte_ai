package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class UserBuilder {
    private UUID id;
    private String phoneNumber;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastInteraction;
    private BigDecimal trustScore = BigDecimal.ONE;
    private Integer totalOccurrences = 0;
    private Integer occurrencesWithPhoto = 0;
    private Boolean isActive = true;
    private Boolean anonymized = false;
    private LocalDateTime anonymizedAt;

    public UserBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public UserBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public UserBuilder lastInteraction(LocalDateTime lastInteraction) {
        this.lastInteraction = lastInteraction;
        return this;
    }

    public UserBuilder trustScore(BigDecimal trustScore) {
        this.trustScore = trustScore;
        return this;
    }

    public UserBuilder totalOccurrences(Integer totalOccurrences) {
        this.totalOccurrences = totalOccurrences;
        return this;
    }

    public UserBuilder occurrencesWithPhoto(Integer occurrencesWithPhoto) {
        this.occurrencesWithPhoto = occurrencesWithPhoto;
        return this;
    }

    public UserBuilder isActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public UserBuilder anonymized(Boolean anonymized) {
        this.anonymized = anonymized;
        return this;
    }

    public UserBuilder anonymizedAt(LocalDateTime anonymizedAt) {
        this.anonymizedAt = anonymizedAt;
        return this;
    }

    public User build() {
        validateRequired();
        return new User(
            id,
            phoneNumber,
            name,
            email,
            createdAt,
            updatedAt,
            lastInteraction,
            trustScore,
            totalOccurrences,
            occurrencesWithPhoto,
            isActive,
            anonymized,
            anonymizedAt
        );
    }

    private void validateRequired() {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
    }
}
