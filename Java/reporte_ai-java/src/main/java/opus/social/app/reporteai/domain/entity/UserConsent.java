package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserConsent {
    private final UUID id;
    private final UUID userId;
    private String consentType;
    private Boolean accepted;
    private LocalDateTime consentDate;
    private String documentVersion;
    private String ipAddress;

    public UserConsent(
        UUID id,
        UUID userId,
        String consentType,
        Boolean accepted,
        LocalDateTime consentDate,
        String documentVersion,
        String ipAddress
    ) {
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

    public UUID getUserId() {
        return userId;
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
