package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entidade JPA para persistência de roles de autenticação
 */
@Entity
@Table(name = "auth_roles")
public class AuthRoleJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "role_name", nullable = false, unique = true, length = 100)
    private String roleName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public AuthRoleJpaEntity() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String roleName;
        private String description;
        private Boolean isActive;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder roleName(String roleName) { this.roleName = roleName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }

        public AuthRoleJpaEntity build() {
            AuthRoleJpaEntity e = new AuthRoleJpaEntity();
            e.id = this.id;
            e.roleName = this.roleName;
            e.description = this.description;
            e.isActive = this.isActive;
            return e;
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
