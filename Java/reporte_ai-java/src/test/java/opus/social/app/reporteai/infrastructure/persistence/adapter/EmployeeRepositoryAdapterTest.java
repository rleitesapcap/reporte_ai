package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.Employee;
import opus.social.app.reporteai.infrastructure.persistence.entity.EmployeeJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.EmployeeJpaRepository;
import opus.social.app.reporteai.infrastructure.persistence.adapter.EmployeeRepositoryAdapter;
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

/**
 * Testes para EmployeeRepositoryAdapter
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeRepositoryAdapter Tests")
class EmployeeRepositoryAdapterTest {

    @Mock
    private EmployeeJpaRepository jpaRepository;

    @InjectMocks
    private EmployeeRepositoryAdapter adapter;

    private Employee validEmployee;
    private EmployeeJpaEntity validJpaEntity;

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

        validJpaEntity = new EmployeeJpaEntity(
            "João Silva",
            "joao@example.com",
            "12345678901",
            "Desenvolvedor",
            5000.0,
            "TI"
        );
        validJpaEntity.setId(1L);
    }

    @Test
    @DisplayName("Deve salvar funcionário com sucesso")
    void testSaveSuccess() {
        // Arrange
        when(jpaRepository.save(any(EmployeeJpaEntity.class)))
            .thenReturn(validJpaEntity);

        // Act
        Employee saved = adapter.save(validEmployee);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getName()).isEqualTo("João Silva");
        verify(jpaRepository).save(any(EmployeeJpaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar funcionário por ID")
    void testFindByIdSuccess() {
        // Arrange
        when(jpaRepository.findById(1L))
            .thenReturn(Optional.of(validJpaEntity));

        // Act
        Optional<Employee> found = adapter.findById(1L);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve retornar empty ao buscar ID inexistente")
    void testFindByIdNotFound() {
        // Arrange
        when(jpaRepository.findById(999L))
            .thenReturn(Optional.empty());

        // Act
        Optional<Employee> found = adapter.findById(999L);

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar funcionário por email")
    void testFindByEmailSuccess() {
        // Arrange
        when(jpaRepository.findByEmail("joao@example.com"))
            .thenReturn(Optional.of(validJpaEntity));

        // Act
        Optional<Employee> found = adapter.findByEmail("joao@example.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("joao@example.com");
    }

    @Test
    @DisplayName("Deve buscar funcionário por CPF")
    void testFindByCpfSuccess() {
        // Arrange
        when(jpaRepository.findByCpf("12345678901"))
            .thenReturn(Optional.of(validJpaEntity));

        // Act
        Optional<Employee> found = adapter.findByCpf("12345678901");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getCpf()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("Deve listar todos os funcionários ativos")
    void testFindAllActive() {
        // Arrange
        List<EmployeeJpaEntity> entities = List.of(validJpaEntity);
        when(jpaRepository.findAllActive())
            .thenReturn(entities);

        // Act
        List<Employee> employees = adapter.findAllActive();

        // Assert
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve listar todos os funcionários")
    void testFindAll() {
        // Arrange
        List<EmployeeJpaEntity> entities = List.of(validJpaEntity);
        when(jpaRepository.findAll())
            .thenReturn(entities);

        // Act
        List<Employee> employees = adapter.findAll();

        // Assert
        assertThat(employees).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar funcionários por departamento")
    void testFindByDepartment() {
        // Arrange
        List<EmployeeJpaEntity> entities = List.of(validJpaEntity);
        when(jpaRepository.findByDepartment("TI"))
            .thenReturn(entities);

        // Act
        List<Employee> employees = adapter.findByDepartment("TI");

        // Assert
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getDepartment()).isEqualTo("TI");
    }

    @Test
    @DisplayName("Deve verificar existência por ID")
    void testExistsById() {
        // Arrange
        when(jpaRepository.existsById(1L))
            .thenReturn(true);

        // Act
        boolean exists = adapter.existsById(1L);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve deletar funcionário")
    void testDelete() {
        // Act
        adapter.delete(1L);

        // Assert
        verify(jpaRepository).deleteById(1L);
    }
}