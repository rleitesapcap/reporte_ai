package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "user_consent",
    indexes = {
        @Index(name = "idx_user_consent_user_id", columnList = "user_id"),
        @Index(name = "idx_user_consent_type", columnList = "consent_type")
    }
)
public class UserConsentJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String consentType;

    @Column(nullable = false)
    private Boolean accepted;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime consentDate;

    @Column(length = 50)
    private String documentVersion;

    @Column(length = 50)
    private String ipAddress;

    public UserConsentJpaEntity() {
    }

    public UserConsentJpaEntity(UUID id, UUID userId, String consentType, Boolean accepted, LocalDateTime consentDate, String documentVersion, String ipAddress) {
        this.id = id;
        this.userId = userId;
        this.consentType = consentType;
        this.accepted = accepted;
        this.consentDate = consentDate;
        this.documentVersion = documentVersion;
        this.ipAddress = ipAddress;
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

    public String getConsentType() {
        return consentType;
    }

    public void setConsentType(String consentType) {
        this.consentType = consentType;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public LocalDateTime getConsentDate() {
        return consentDate;
    }

    public String getDocumentVersion() {
        return documentVersion;
    }

    public void setDocumentVersion(String documentVersion) {
        this.documentVersion = documentVersion;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
