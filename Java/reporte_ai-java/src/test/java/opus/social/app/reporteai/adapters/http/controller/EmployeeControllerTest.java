package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.CreateEmployeeRequest;
import opus.social.app.reporteai.application.dto.EmployeeResponse;
import opus.social.app.reporteai.application.service.EmployeeApplicationService;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import opus.social.app.reporteai.domain.factory.exception.BusinessExceptionHandlerStrategy;
import opus.social.app.reporteai.domain.factory.exception.DuplicateDataHandlerStrategy;
import opus.social.app.reporteai.domain.factory.exception.EmployeeNotFoundHandlerStrategy;
import opus.social.app.reporteai.domain.factory.exception.ExceptionHandlerRegistry;
import opus.social.app.reporteai.domain.factory.exception.ExceptionResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para EmployeeController com MockMvc
 */
@WebMvcTest(EmployeeController.class)
@Import({
    ExceptionResponseFactory.class,
    ExceptionHandlerRegistry.class,
    EmployeeNotFoundHandlerStrategy.class,
    DuplicateDataHandlerStrategy.class,
    BusinessExceptionHandlerStrategy.class
})
@DisplayName("EmployeeController Tests")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeApplicationService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeResponse validResponse;
    private CreateEmployeeRequest validRequest;

    @BeforeEach
    void setUp() {
        validResponse = new EmployeeResponse(
            1L,
            "João Silva",
            "joao@example.com",
            "12345678901",
            "Desenvolvedor",
            5000.0,
            "TI",
            LocalDateTime.now(),
            LocalDateTime.now(),
            true
        );

        validRequest = new CreateEmployeeRequest(
            "João Silva",
            "joao@example.com",
            "12345678901",
            "Desenvolvedor",
            5000.0,
            "TI"
        );
    }

    @Test
    @DisplayName("Deve criar novo funcionário com status 201")
    void testCreateEmployeeSuccess() throws Exception {
        // Arrange
        when(employeeService.createEmployee(any(CreateEmployeeRequest.class)))
            .thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("João Silva"))
            .andExpect(jsonPath("$.email").value("joao@example.com"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao enviar email inválido")
    void testCreateEmployeeWithInvalidEmail() throws Exception {
        // Arrange
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest(
            "João Silva",
            "email-invalido",
            "12345678901",
            "Desenvolvedor",
            5000.0,
            "TI"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve buscar funcionário por ID com sucesso")
    void testGetEmployeeByIdSuccess() throws Exception {
        // Arrange
        when(employeeService.getEmployeeById(1L))
            .thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("João Silva"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar funcionário inexistente")
    void testGetEmployeeByIdNotFound() throws Exception {
        // Arrange
        when(employeeService.getEmployeeById(999L))
            .thenThrow(new EmployeeNotFoundException(999L));

        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar todos os funcionários ativos")
    void testListActiveEmployees() throws Exception {
        // Arrange
        List<EmployeeResponse> employees = List.of(validResponse);
        when(employeeService.listActiveEmployees())
            .thenReturn(employees);

        // Act & Assert
        mockMvc.perform(get("/api/v1/employees")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("João Silva"));
    }

    @Test
    @DisplayName("Deve listar funcionários por departamento")
    void testGetEmployeesByDepartment() throws Exception {
        // Arrange
        List<EmployeeResponse> employees = List.of(validResponse);
        when(employeeService.getEmployeesByDepartment("TI"))
            .thenReturn(employees);

        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/department/TI")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].department").value("TI"));
    }

    @Test
    @DisplayName("Deve deletar funcionário com status 204")
    void testDeleteEmployeeSuccess() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/employees/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve reativar funcionário")
    void testReactivateEmployee() throws Exception {
        // Arrange
        when(employeeService.reactivateEmployee(1L))
            .thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/employees/1/reactivate")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(true));
    }
}