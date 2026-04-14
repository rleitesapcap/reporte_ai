package opus.social.app.reporteai.application.specification;

import java.time.LocalDateTime;

/**
 * Query Object para busca de usuários
 * Repository Pattern - Encapsula critérios de busca complexos
 *
 * Permite construir queries complexas de forma type-safe
 */
public class UserSearchSpecification {
    private String username;
    private String email;
    private String fullName;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private Boolean isActive;
    private Boolean isLocked;

    // ===== Getters =====
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDateTime getCreatedAfter() {
        return createdAfter;
    }

    public LocalDateTime getCreatedBefore() {
        return createdBefore;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    // ===== Builders fluentes =====
    public UserSearchSpecification withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserSearchSpecification withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserSearchSpecification withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public UserSearchSpecification withCreatedAfter(LocalDateTime createdAfter) {
        this.createdAfter = createdAfter;
        return this;
    }

    public UserSearchSpecification withCreatedBefore(LocalDateTime createdBefore) {
        this.createdBefore = createdBefore;
        return this;
    }

    public UserSearchSpecification withIsActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public UserSearchSpecification withIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
        return this;
    }

    // ===== Factory methods =====
    public static UserSearchSpecification byUsername(String username) {
        return new UserSearchSpecification().withUsername(username);
    }

    public static UserSearchSpecification byEmail(String email) {
        return new UserSearchSpecification().withEmail(email);
    }

    public static UserSearchSpecification activeUsers() {
        return new UserSearchSpecification().withIsActive(true);
    }

    public static UserSearchSpecification lockedUsers() {
        return new UserSearchSpecification().withIsLocked(true);
    }

    public static UserSearchSpecification create() {
        return new UserSearchSpecification();
    }

    @Override
    public String toString() {
        return "UserSearchSpecification{" +
            "username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", fullName='" + fullName + '\'' +
            ", isActive=" + isActive +
            ", isLocked=" + isLocked +
            '}';
    }
}
