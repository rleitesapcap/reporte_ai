package opus.social.app.reporteai.domain.entity;

import java.util.UUID;

/**
 * Entidade de domínio para Roles de autenticação
 * Representa as roles (funções) disponíveis no sistema
 */
public class AuthRole {
    private final UUID id;
    private String roleName;
    private String description;
    private Boolean isActive;

    public AuthRole(UUID id, String roleName, String description, Boolean isActive) {
        this.id = id;
        this.roleName = roleName;
        this.description = description;
        this.isActive = isActive;
    }

    public UUID getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
