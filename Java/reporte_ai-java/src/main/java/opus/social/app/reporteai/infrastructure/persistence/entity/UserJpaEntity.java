package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_phone", columnList = "phone_number"),
        @Index(name = "idx_users_created_at", columnList = "created_at"),
        @Index(name = "idx_users_is_active", columnList = "is_active")
    }
)
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String email;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime lastInteraction;

    @Column(precision = 4, scale = 2)
    private BigDecimal trustScore = BigDecimal.ONE;

    @Column
    private Integer totalOccurrences = 0;

    @Column
    private Integer occurrencesWithPhoto = 0;

    @Column
    private Boolean isActive = true;

    @Column
    private Boolean anonymized = false;

    @Column
    private LocalDateTime anonymizedAt;

    public UserJpaEntity() {
    }

    public UserJpaEntity(
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
