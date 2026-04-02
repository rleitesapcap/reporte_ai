package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.OccurrenceHistoryResponse;
import opus.social.app.reporteai.application.service.OccurrenceHistoryApplicationService;
import opus.social.app.reporteai.domain.entity.OccurrenceHistory;
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
 * Controller REST para Histórico de Ocorrência
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/occurrence-history")
@Tag(name = "Histórico de Ocorrência", description = "Endpoints para gestão do histórico de ocorrências")
public class OccurrenceHistoryController {

    private final OccurrenceHistoryApplicationService historyService;

    public OccurrenceHistoryController(OccurrenceHistoryApplicationService historyService) {
        this.historyService = historyService;
    }

    /**
     * Registrar mudança de ocorrência
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Registrar mudança", description = "Registra uma mudança no histórico da ocorrência")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Mudança registrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Ocorrência não encontrada")
    })
    public ResponseEntity<OccurrenceHistoryResponse> recordChange(
            @RequestParam UUID occurrenceId,
            @RequestParam String action,
            @RequestParam String oldStatus,
            @RequestParam String newStatus,
            @RequestParam(required = false) BigDecimal oldScore,
            @RequestParam(required = false) BigDecimal newScore,
            @RequestParam(required = false) String reason) {
        OccurrenceHistory history = historyService.recordChange(
            occurrenceId, action, oldStatus, newStatus, oldScore, newScore, reason
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(history));
    }

    /**
     * Buscar histórico de uma ocorrência
     */
    @GetMapping("/occurrence/{occurrenceId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar histórico", description = "Retorna o histórico de uma ocorrência")
    @ApiResponse(responseCode = "200", description = "Histórico encontrado")
    public ResponseEntity<List<OccurrenceHistoryResponse>> getOccurrenceHistory(
            @PathVariable UUID occurrenceId) {
        List<OccurrenceHistory> history = historyService.getOccurrenceHistory(occurrenceId);
        return ResponseEntity.ok(
            history.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Deletar entrada do histórico
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar entrada", description = "Remove uma entrada do histórico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Entrada deletada"),
        @ApiResponse(responseCode = "404", description = "Entrada não encontrada")
    })
    public ResponseEntity<Void> deleteHistory(@PathVariable UUID id) {
        historyService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }

    private OccurrenceHistoryResponse toResponse(OccurrenceHistory history) {
        return new OccurrenceHistoryResponse(
            history.getId(),
            history.getOccurrenceId(),
            history.getUserId(),
            history.getAction(),
            history.getOldStatus(),
            history.getNewStatus(),
            history.getOldScore(),
            history.getNewScore(),
            history.getReason(),
            history.getChangedAt()
        );
    }
}
