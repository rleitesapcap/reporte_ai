package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.AuthRoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para roles de autenticação
 */
@Repository
public interface AuthRoleRepository extends JpaRepository<AuthRoleJpaEntity, UUID> {
    
    Optional<AuthRoleJpaEntity> findByRoleName(String roleName);
    
    List<AuthRoleJpaEntity> findAllByIsActive(Boolean isActive);
    
    boolean existsByRoleName(String roleName);
}
