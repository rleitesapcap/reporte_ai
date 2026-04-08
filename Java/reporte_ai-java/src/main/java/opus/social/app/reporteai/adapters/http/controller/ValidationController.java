package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.ValidationCreateRequest;
import opus.social.app.reporteai.application.dto.ValidationResponse;
import opus.social.app.reporteai.application.service.ValidationApplicationService;
import opus.social.app.reporteai.domain.entity.Validation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Validações
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/validations")
@Tag(name = "Validações", description = "Endpoints para gestão de validações")
public class ValidationController {

    private final ValidationApplicationService validationService;

    public ValidationController(ValidationApplicationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Criar nova validação
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VALIDATOR')")
    @Operation(summary = "Criar validação", description = "Cria uma nova validação para uma ocorrência")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Validação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Ocorrência não encontrada")
    })
    public ResponseEntity<ValidationResponse> createValidation(
            @Valid @RequestBody ValidationCreateRequest request) {
        Validation validation = validationService.createValidation(
            request.getOccurrenceId(),
            request.getValidationType(),
            request.getResult(),
            request.getReason(),
            request.getConfidence()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(validation));
    }

    /**
     * Buscar validação por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar validação", description = "Retorna os dados de uma validação específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validação encontrada"),
        @ApiResponse(responseCode = "404", description = "Validação não encontrada")
    })
    public ResponseEntity<ValidationResponse> getValidationById(@PathVariable UUID id) {
        Validation validation = validationService.getValidationById(id);
        return ResponseEntity.ok(toResponse(validation));
    }

    /**
     * Buscar validações por ocorrência
     */
    @GetMapping("/occurrence/{occurrenceId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar validações", description = "Retorna validações de uma ocorrência")
    @ApiResponse(responseCode = "200", description = "Validações encontradas")
    public ResponseEntity<List<ValidationResponse>> getValidationsByOccurrence(
            @PathVariable UUID occurrenceId) {
        List<Validation> validations = validationService.getValidationsByOccurrence(occurrenceId);
        return ResponseEntity.ok(
            validations.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Buscar validações por resultado
     */
    @GetMapping("/result/{result}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar por resultado", description = "Retorna validações com um resultado específico")
    @ApiResponse(responseCode = "200", description = "Validações encontradas")
    public ResponseEntity<List<ValidationResponse>> getValidationsByResult(@PathVariable String result) {
        List<Validation> validations = validationService.getValidationsByResult(result);
        return ResponseEntity.ok(
            validations.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Atualizar resultado da validação
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VALIDATOR')")
    @Operation(summary = "Atualizar validação", description = "Atualiza o resultado de uma validação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validação atualizada"),
        @ApiResponse(responseCode = "404", description = "Validação não encontrada")
    })
    public ResponseEntity<ValidationResponse> updateValidationResult(
            @PathVariable UUID id,
            @RequestParam String result,
            @RequestParam(required = false) String reason) {
        Validation validation = validationService.updateValidationResult(id, result, reason);
        return ResponseEntity.ok(toResponse(validation));
    }

    /**
     * Deletar validação
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar validação", description = "Remove uma validação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Validação deletada"),
        @ApiResponse(responseCode = "404", description = "Validação não encontrada")
    })
    public ResponseEntity<Void> deleteValidation(@PathVariable UUID id) {
        validationService.deleteValidation(id);
        return ResponseEntity.noContent().build();
    }

    private ValidationResponse toResponse(Validation validation) {
        return new ValidationResponse(
            validation.getId(),
            validation.getOccurrenceId(),
            validation.getValidatorUserId(),
            validation.getValidationType(),
            validation.getResult(),
            validation.getReason(),
            validation.getConfidence(),
            validation.getValidatedAt()
        );
    }
}
