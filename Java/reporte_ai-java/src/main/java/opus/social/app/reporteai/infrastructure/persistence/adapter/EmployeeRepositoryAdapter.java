package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.Employee;
import opus.social.app.reporteai.domain.port.EmployeeRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.EmployeeJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.EmployeeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter (ou Implementação) da Port EmployeeRepositoryPort
 * Implementa a interface do domínio usando Spring Data JPA
 * Esta classe adapta a entidade JPA para a entidade de domínio
 */
@Component
public class EmployeeRepositoryAdapter implements EmployeeRepositoryPort {

    private final EmployeeJpaRepository jpaRepository;

    public EmployeeRepositoryAdapter(EmployeeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Employee save(Employee employee) {
        EmployeeJpaEntity entity = toDomainEntity(employee);
        EmployeeJpaEntity saved = jpaRepository.save(entity);
        return toEmployee(saved);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return jpaRepository.findById(id)
            .map(this::toEmployee);
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
            .map(this::toEmployee);
    }

    @Override
    public Optional<Employee> findByCpf(String cpf) {
        return jpaRepository.findByCpf(cpf)
            .map(this::toEmployee);
    }

    @Override
    public List<Employee> findAllActive() {
        return jpaRepository.findAllActive()
            .stream()
            .map(this::toEmployee)
            .toList();
    }

    @Override
    public List<Employee> findAll() {
        return jpaRepository.findAll()
            .stream()
            .map(this::toEmployee)
            .toList();
    }

    @Override
    public List<Employee> findByDepartment(String department) {
        return jpaRepository.findByDepartment(department)
            .stream()
            .map(this::toEmployee)
            .toList();
    }

    @Override
    public Employee update(Employee employee) {
        EmployeeJpaEntity entity = toDomainEntity(employee);
        EmployeeJpaEntity updated = jpaRepository.save(entity);
        return toEmployee(updated);
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    // ============ Conversões entre Domain Entity e JPA Entity ============

    /**
     * Converte de Employee (Domain) para EmployeeJpaEntity
     */
    private EmployeeJpaEntity toDomainEntity(Employee employee) {
        EmployeeJpaEntity entity = new EmployeeJpaEntity(
            employee.getName(),
            employee.getEmail(),
            employee.getCpf(),
            employee.getPosition(),
            employee.getSalary(),
            employee.getDepartment()
        );

        if (employee.getId() != null) {
            entity.setId(employee.getId());
        }

        entity.setCreatedAt(employee.getCreatedAt());
        entity.setUpdatedAt(employee.getUpdatedAt());
        entity.setActive(employee.isActive());

        return entity;
    }

    /**
     * Converte de EmployeeJpaEntity para Employee (Domain)
     */
    private Employee toEmployee(EmployeeJpaEntity entity) {
        Employee employee = new Employee(
            entity.getName(),
            entity.getEmail(),
            entity.getCpf(),
            entity.getPosition(),
            entity.getSalary(),
            entity.getDepartment()
        );

        employee.setId(entity.getId());
        employee.setCreatedAt(entity.getCreatedAt());
        employee.setUpdatedAt(entity.getUpdatedAt());
        employee.setActive(entity.isActive());

        return employee;
    }
}