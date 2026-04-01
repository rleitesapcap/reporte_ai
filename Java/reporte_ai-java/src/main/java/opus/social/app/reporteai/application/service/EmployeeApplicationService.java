package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.application.dto.CreateEmployeeRequest;
import opus.social.app.reporteai.application.dto.UpdateEmployeeRequest;
import opus.social.app.reporteai.application.dto.EmployeeResponse;
import opus.social.app.reporteai.domain.entity.Employee;
import opus.social.app.reporteai.domain.entity.EmployeeBuilder;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import opus.social.app.reporteai.domain.port.EmployeeRepositoryPort;
import opus.social.app.reporteai.domain.strategy.validation.ValidationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Aplicação para Funcionários
 * Contém os casos de uso (use cases) de funcionários
 * Orquestra o domínio e utiliza o repositório (port) para persistência
 */
@Service
public class EmployeeApplicationService {

    private final EmployeeRepositoryPort employeeRepository;
    private final ValidationContext validationContext;

    public EmployeeApplicationService(EmployeeRepositoryPort employeeRepository,
                                    ValidationContext validationContext) {
        this.employeeRepository = employeeRepository;
        this.validationContext = validationContext;
    }

    /**
     * Caso de uso: Criar novo funcionário
     */
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        // Validações de negócio usando Strategy Pattern
        validationContext.validateEmail(request.getEmail());
        validationContext.validateCpf(request.getCpf());

        // Criar entidade de domínio usando Builder Pattern
        Employee employee = Employee.builder()
            .withName(request.getName())
            .withEmail(request.getEmail())
            .withCpf(request.getCpf())
            .withPosition(request.getPosition())
            .withSalary(request.getSalary())
            .withDepartment(request.getDepartment())
            .build();

        // Persistir
        Employee savedEmployee = employeeRepository.save(employee);

        return toResponse(savedEmployee);
    }

    /**
     * Caso de uso: Buscar funcionário por ID
     */
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));
        return toResponse(employee);
    }

    /**
     * Caso de uso: Listar todos os funcionários ativos
     */
    public List<EmployeeResponse> listActiveEmployees() {
        return employeeRepository.findAllActive()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Caso de uso: Listar todos os funcionários
     */
    public List<EmployeeResponse> listAllEmployees() {
        return employeeRepository.findAll()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Caso de uso: Buscar funcionários por departamento
     */
    public List<EmployeeResponse> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Caso de uso: Atualizar funcionário
     */
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));

        // Validar email se foi alterado
        if (!employee.getEmail().equals(request.getEmail())) {
            validationContext.validateEmail(request.getEmail());
        }

        // Atualizar campos
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPosition(request.getPosition());
        employee.setSalary(request.getSalary());
        employee.setDepartment(request.getDepartment());
        employee.setUpdatedAt(LocalDateTime.now());

        Employee updated = employeeRepository.update(employee);
        return toResponse(updated);
    }

    /**
     * Caso de uso: Deletar funcionário (soft delete)
     */
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));

        employee.setActive(false);
        employee.setUpdatedAt(LocalDateTime.now());
        employeeRepository.update(employee);
    }

    /**
     * Caso de uso: Reativar funcionário
     */
    public EmployeeResponse reactivateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));

        employee.setActive(true);
        employee.setUpdatedAt(LocalDateTime.now());
        Employee updated = employeeRepository.update(employee);
        return toResponse(updated);
    }


    // ============ Conversões ============

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
            employee.getId(),
            employee.getName(),
            employee.getEmail(),
            employee.getCpf(),
            employee.getPosition(),
            employee.getSalary(),
            employee.getDepartment(),
            employee.getCreatedAt(),
            employee.getUpdatedAt(),
            employee.isActive()
        );
    }
}