package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserJpaEntity> findByEmail(String email);
    
    @Query("SELECT u FROM UserJpaEntity u WHERE u.isActive = true")
    List<UserJpaEntity> findAllActive();
    
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
