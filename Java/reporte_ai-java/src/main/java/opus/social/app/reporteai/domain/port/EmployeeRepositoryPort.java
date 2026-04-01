package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.Employee;
import java.util.List;
import java.util.Optional;

/**
 * Port: Interface de contrato para persistência de Funcionários
 * Esta é uma porta na arquitetura hexagonal
 * Implementações desta interface ficam na camada de Infrastructure
 */
public interface EmployeeRepositoryPort {

    /**
     * Salva um funcionário no banco de dados
     */
    Employee save(Employee employee);

    /**
     * Busca um funcionário por ID
     */
    Optional<Employee> findById(Long id);

    /**
     * Busca um funcionário por email
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Busca um funcionário por CPF
     */
    Optional<Employee> findByCpf(String cpf);

    /**
     * Retorna todos os funcionários ativos
     */
    List<Employee> findAllActive();

    /**
     * Retorna todos os funcionários
     */
    List<Employee> findAll();

    /**
     * Busca funcionários por departamento
     */
    List<Employee> findByDepartment(String department);

    /**
     * Atualiza um funcionário
     */
    Employee update(Employee employee);

    /**
     * Deleta um funcionário
     */
    void delete(Long id);

    /**
     * Verifica se um funcionário existe por ID
     */
    boolean existsById(Long id);
}