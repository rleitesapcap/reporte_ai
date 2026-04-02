package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.AuthUserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para usuários de autenticação
 */
@Repository
public interface AuthUserRepository extends JpaRepository<AuthUserJpaEntity, UUID> {
    
    Optional<AuthUserJpaEntity> findByUsername(String username);
    
    Optional<AuthUserJpaEntity> findByEmail(String email);
    
    Optional<AuthUserJpaEntity> findByUsernameOrEmail(String username, String email);
    
    List<AuthUserJpaEntity> findAllByIsActive(Boolean isActive);
    
    List<AuthUserJpaEntity> findAllByIsLocked(Boolean isLocked);
    
    @Query("SELECT u FROM AuthUserJpaEntity u WHERE u.isActive = true ORDER BY u.createdAt DESC")
    List<AuthUserJpaEntity> findAllActiveUsers();
    
    @Query("SELECT u FROM AuthUserJpaEntity u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<AuthUserJpaEntity> findByUsernameWithRoles(@Param("username") String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
