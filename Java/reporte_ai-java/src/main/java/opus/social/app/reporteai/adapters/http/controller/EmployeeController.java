package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.CreateEmployeeRequest;
import opus.social.app.reporteai.application.dto.UpdateEmployeeRequest;
import opus.social.app.reporteai.application.dto.EmployeeResponse;
import opus.social.app.reporteai.application.service.EmployeeApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para Funcionários
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Funcionários", description = "Endpoints para gestão de funcionários")
public class EmployeeController {

    private final EmployeeApplicationService employeeService;

    public EmployeeController(EmployeeApplicationService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Criar novo funcionário
     */
    @PostMapping
    @Operation(summary = "Criar novo funcionário", description = "Cria um novo registro de funcionário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Buscar funcionário por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar funcionário por ID", description = "Retorna os dados de um funcionário específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Funcionário encontrado"),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Listar todos os funcionários ativos
     */
    @GetMapping
    @Operation(summary = "Listar funcionários ativos", description = "Retorna lista de todos os funcionários ativos")
    @ApiResponse(responseCode = "200", description = "Lista de funcionários")
    public ResponseEntity<List<EmployeeResponse>> listActiveEmployees() {
        List<EmployeeResponse> responses = employeeService.listActiveEmployees();
        return ResponseEntity.ok(responses);
    }

    /**
     * Listar todos os funcionários (inclusive inativos)
     */
    @GetMapping("/all")
    @Operation(summary = "Listar todos os funcionários", description = "Retorna lista de todos os funcionários, ativos e inativos")
    @ApiResponse(responseCode = "200", description = "Lista de funcionários")
    public ResponseEntity<List<EmployeeResponse>> listAllEmployees() {
        List<EmployeeResponse> responses = employeeService.listAllEmployees();
        return ResponseEntity.ok(responses);
    }

    /**
     * Buscar funcionários por departamento
     */
    @GetMapping("/department/{department}")
    @Operation(summary = "Buscar por departamento", description = "Retorna funcionários de um departamento específico")
    @ApiResponse(responseCode = "200", description = "Lista de funcionários do departamento")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(@PathVariable String department) {
        List<EmployeeResponse> responses = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(responses);
    }

    /**
     * Atualizar funcionário
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar funcionário", description = "Atualiza os dados de um funcionário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado"),
        @ApiResponse(responseCode = "409", description = "Email já cadastrado para outro funcionário")
    })
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateEmployeeRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletar funcionário (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar funcionário", description = "Marca um funcionário como inativo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Funcionário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reativar funcionário
     */
    @PostMapping("/{id}/reactivate")
    @Operation(summary = "Reativar funcionário", description = "Marca um funcionário inativo como ativo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Funcionário reativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    public ResponseEntity<EmployeeResponse> reactivateEmployee(@PathVariable Long id) {
        EmployeeResponse response = employeeService.reactivateEmployee(id);
        return ResponseEntity.ok(response);
    }
}