package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.ReportResponse;
import opus.social.app.reporteai.application.service.ReportApplicationService;
import opus.social.app.reporteai.domain.entity.Report;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Relatórios
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Relatórios", description = "Endpoints para gestão de relatórios")
public class ReportController {

    private final ReportApplicationService reportService;

    public ReportController(ReportApplicationService reportService) {
        this.reportService = reportService;
    }

    /**
     * Criar novo relatório
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('REPORT_CREATOR')")
    @Operation(summary = "Criar relatório", description = "Cria um novo relatório")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Relatório criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ReportResponse> createReport(
            @RequestParam String title,
            @RequestParam String type,
            @RequestParam(required = false) String description,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Report report = reportService.createReport(title, type, description, startDate, endDate);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(report));
    }

    /**
     * Buscar relatórios por tipo
     */
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar relatórios por tipo", description = "Retorna relatórios de um tipo específico")
    @ApiResponse(responseCode = "200", description = "Relatórios encontrados")
    public ResponseEntity<List<ReportResponse>> getReportsByType(@PathVariable String type) {
        List<Report> reports = reportService.getReportsByType(type);
        return ResponseEntity.ok(
            reports.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Buscar relatórios por status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar relatórios por status", description = "Retorna relatórios com um status específico")
    @ApiResponse(responseCode = "200", description = "Relatórios encontrados")
    public ResponseEntity<List<ReportResponse>> getReportsByStatus(@PathVariable String status) {
        List<Report> reports = reportService.getReportsByStatus(status);
        return ResponseEntity.ok(
            reports.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Atualizar status do relatório
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REPORT_CREATOR')")
    @Operation(summary = "Atualizar status", description = "Atualiza o status de um relatório")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado"),
        @ApiResponse(responseCode = "404", description = "Relatório não encontrado")
    })
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        Report report = reportService.updateReportStatus(id, status);
        return ResponseEntity.ok(toResponse(report));
    }

    /**
     * Deletar relatório
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar relatório", description = "Remove um relatório")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Relatório deletado"),
        @ApiResponse(responseCode = "404", description = "Relatório não encontrado")
    })
    public ResponseEntity<Void> deleteReport(@PathVariable UUID id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    private ReportResponse toResponse(Report report) {
        return new ReportResponse(
            report.getId(),
            report.getTitle(),
            report.getReportType(),
            report.getDescription(),
            report.getFilePath(),
            report.getFileSize(),
            report.getPeriodStart(),
            report.getPeriodEnd(),
            report.getGeneratedAt(),
            report.getStatus()
        );
    }
}
