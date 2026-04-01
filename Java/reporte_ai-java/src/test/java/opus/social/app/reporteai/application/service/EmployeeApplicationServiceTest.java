package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.application.dto.CreateEmployeeRequest;
import opus.social.app.reporteai.application.dto.UpdateEmployeeRequest;
import opus.social.app.reporteai.application.dto.EmployeeResponse;
import opus.social.app.reporteai.domain.entity.Employee;
import opus.social.app.reporteai.domain.exception.DuplicateDataException;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import opus.social.app.reporteai.domain.port.EmployeeRepositoryPort;
import opus.social.app.reporteai.domain.strategy.validation.ValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

/**
 * Testes unitários para EmployeeApplicationService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeApplicationService Tests")
class EmployeeApplicationServiceTest {

    @Mock
    private EmployeeRepositoryPort employeeRepository;

    @Mock
    private ValidationContext validationContext;

    @InjectMocks
    private EmployeeApplicationService employeeService;

    private Employee validEmployee;
    private CreateEmployeeRequest validRequest;
    private UpdateEmployeeRequest updateRequest;

    @BeforeEach
    void setUp() {
        validEmployee = new Employee(
            "João Silva",
            "joao@example.com",
            "12345678901",
            "Desenvolvedor",
            5000.0,
            "TI"
        );
        validEmployee.setId(1L);
        validEmployee.setCreatedAt(LocalDateTime.now());

        validRequest = new CreateEmployeeRequest(
            "João Silva",
            "joao@example.com",
            "12345678901",
            "Desenvolvedor",
            5000.0,
            "TI"
        );

        updateRequest = new UpdateEmployeeRequest(
            "João Silva Atualizado",
            "joao.novo@example.com",
            "Desenvolvedor Senior",
            6000.0,
            "TI"
        );
    }

    @Test
    @DisplayName("Deve criar novo funcionário com sucesso")
    void testCreateEmployeeSuccess() {
        // Arrange
        when(employeeRepository.save(any(Employee.class))).thenReturn(validEmployee);

        // Act
        EmployeeResponse response = employeeService.createEmployee(validRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("João Silva");
        assertThat(response.getEmail()).isEqualTo("joao@example.com");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("Deve falhar ao criar funcionário com email duplicado")
    void testCreateEmployeeWithDuplicateEmail() {
        // Arrange
        doThrow(new DuplicateDataException("email", validRequest.getEmail()))
            .when(validationContext).validateEmail(validRequest.getEmail());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.createEmployee(validRequest))
            .isInstanceOf(DuplicateDataException.class)
            .hasMessageContaining("email");
    }

    @Test
    @DisplayName("Deve falhar ao criar funcionário com CPF duplicado")
    void testCreateEmployeeWithDuplicateCpf() {
        // Arrange
        doThrow(new DuplicateDataException("CPF", validRequest.getCpf()))
            .when(validationContext).validateCpf(validRequest.getCpf());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.createEmployee(validRequest))
            .isInstanceOf(DuplicateDataException.class)
            .hasMessageContaining("CPF");
    }

    @Test
    @DisplayName("Deve buscar funcionário por ID com sucesso")
    void testGetEmployeeByIdSuccess() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));

        // Act
        EmployeeResponse response = employeeService.getEmployeeById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve falhar ao buscar funcionário não existente")
    void testGetEmployeeByIdNotFound() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.getEmployeeById(999L))
            .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar funcionários ativos")
    void testListActiveEmployees() {
        // Arrange
        List<Employee> employees = List.of(validEmployee);
        when(employeeRepository.findAllActive()).thenReturn(employees);

        // Act
        List<EmployeeResponse> responses = employeeService.listActiveEmployees();

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve atualizar funcionário com sucesso")
    void testUpdateEmployeeSuccess() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(employeeRepository.update(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setUpdatedAt(LocalDateTime.now());
            return employee;
        });

        // Act
        EmployeeResponse response = employeeService.updateEmployee(1L, updateRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("João Silva Atualizado");
        verify(employeeRepository).update(any(Employee.class));
    }

    @Test
    @DisplayName("Deve deletar funcionário (soft delete)")
    void testDeleteEmployee() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(employeeRepository.update(any(Employee.class))).thenReturn(validEmployee);

        // Act
        employeeService.deleteEmployee(1L);

        // Assert
        verify(employeeRepository).update(any(Employee.class));
    }

    @Test
    @DisplayName("Deve reativar funcionário")
    void testReactivateEmployee() {
        // Arrange
        validEmployee.setActive(false);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(employeeRepository.update(any(Employee.class))).thenReturn(validEmployee);

        // Act
        EmployeeResponse response = employeeService.reactivateEmployee(1L);

        // Assert
        assertThat(response).isNotNull();
        verify(employeeRepository).update(any(Employee.class));
    }
}