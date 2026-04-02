package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.DeduplicationRecordResponse;
import opus.social.app.reporteai.application.service.DeduplicationRecordApplicationService;
import opus.social.app.reporteai.domain.entity.DeduplicationRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Registros de Deduplicação
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/deduplication-records")
@Tag(name = "Deduplicação", description = "Endpoints para gestão de registros de deduplicação")
public class DeduplicationRecordController {

    private final DeduplicationRecordApplicationService dedupService;

    public DeduplicationRecordController(DeduplicationRecordApplicationService dedupService) {
        this.dedupService = dedupService;
    }

    /**
     * Registrar ocorrência duplicada
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")
    @Operation(summary = "Registrar duplicado", description = "Registra uma ocorrência como duplicada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Duplicado registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Ocorrência não encontrada")
    })
    public ResponseEntity<DeduplicationRecordResponse> recordDuplicate(
            @RequestParam UUID mainOccurrenceId,
            @RequestParam UUID duplicateId,
            @RequestParam BigDecimal similarity,
            @RequestParam(required = false) BigDecimal distance,
            @RequestParam(required = false) Integer timeDiff,
            @RequestParam(required = false) String method) {
        DeduplicationRecord record = dedupService.recordDuplicate(
            mainOccurrenceId, duplicateId, similarity, distance, timeDiff, method
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(record));
    }

    /**
     * Buscar duplicatas de uma ocorrência
     */
    @GetMapping("/occurrence/{occurrenceId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar duplicatas", description = "Retorna os duplicados de uma ocorrência")
    @ApiResponse(responseCode = "200", description = "Duplicatas encontradas")
    public ResponseEntity<List<DeduplicationRecordResponse>> getDuplicatesForOccurrence(
            @PathVariable UUID occurrenceId) {
        List<DeduplicationRecord> records = dedupService.getDuplicatesForOccurrence(occurrenceId);
        return ResponseEntity.ok(
            records.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Deletar registro de deduplicação
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar registro", description = "Remove um registro de deduplicação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Registro deletado"),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<Void> deleteRecord(@PathVariable UUID id) {
        dedupService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    private DeduplicationRecordResponse toResponse(DeduplicationRecord record) {
        return new DeduplicationRecordResponse(
            record.getId(),
            record.getMainOccurrenceId(),
            record.getDuplicateId(),
            record.getSimilarity(),
            record.getDistance(),
            record.getTimeDifference(),
            record.getManualReview(),
            record.getMethod(),
            record.getRecordedAt()
        );
    }
}
