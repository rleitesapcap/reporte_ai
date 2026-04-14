package opus.social.app.reporteai.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthUserJpaEntity;

public class UserResponse {

    public static UserResponse from(AuthUserJpaEntity entity) {
        return new UserResponse(
            entity.getId(),
            entity.getUsername(),
            entity.getFullName(),
            entity.getEmail(),
            null,
            0,
            entity.getIsActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    private UUID id;
    private String phoneNumber;
    private String name;
    private String email;
    private BigDecimal trustScore;
    private Integer totalOccurrences;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponse() {
    }

    public UserResponse(UUID id, String phoneNumber, String name, String email,
            BigDecimal trustScore, Integer totalOccurrences, Boolean isActive,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.email = email;
        this.trustScore = trustScore;
        this.totalOccurrences = totalOccurrences;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getTrustScore() { return trustScore; }
    public void setTrustScore(BigDecimal trustScore) { this.trustScore = trustScore; }

    public Integer getTotalOccurrences() { return totalOccurrences; }
    public void setTotalOccurrences(Integer totalOccurrences) { this.totalOccurrences = totalOccurrences; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
