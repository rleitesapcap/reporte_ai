package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.infrastructure.persistence.entity.EmployeeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository para EmployeeJpaEntity
 * Fornece operações básicas e customizadas de persistência
 */
@Repository
public interface EmployeeJpaRepository extends JpaRepository<EmployeeJpaEntity, Long> {

    Optional<EmployeeJpaEntity> findByEmail(String email);

    Optional<EmployeeJpaEntity> findByCpf(String cpf);

    @Query("SELECT e FROM EmployeeJpaEntity e WHERE e.active = true")
    List<EmployeeJpaEntity> findAllActive();

    @Query("SELECT e FROM EmployeeJpaEntity e WHERE e.department = :department AND e.active = true")
    List<EmployeeJpaEntity> findByDepartment(@Param("department") String department);
}