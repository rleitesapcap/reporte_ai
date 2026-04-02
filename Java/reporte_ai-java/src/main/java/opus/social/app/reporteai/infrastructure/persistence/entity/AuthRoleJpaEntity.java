package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade JPA para persistência de roles de autenticação
 */
@Entity
@Table(name = "auth_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
