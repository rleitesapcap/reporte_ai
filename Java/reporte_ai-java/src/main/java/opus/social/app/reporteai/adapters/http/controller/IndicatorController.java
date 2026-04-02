package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.IndicatorResponse;
import opus.social.app.reporteai.application.service.IndicatorApplicationService;
import opus.social.app.reporteai.domain.entity.Indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Indicadores
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/indicators")
@Tag(name = "Indicadores", description = "Endpoints para gestão de indicadores")
public class IndicatorController {

    private final IndicatorApplicationService indicatorService;

    public IndicatorController(IndicatorApplicationService indicatorService) {
        this.indicatorService = indicatorService;
    }

    /**
     * Criar novo indicador
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")
    @Operation(summary = "Criar indicador", description = "Cria um novo indicador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Indicador criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<IndicatorResponse> createIndicator(
            @RequestParam String name,
            @RequestParam String type,
            @RequestParam BigDecimal value,
            @RequestParam String unit,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Indicator indicator = indicatorService.createIndicator(
            name, type, value, unit, startDate, endDate
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(indicator));
    }

    /**
     * Buscar indicadores por tipo
     */
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar indicadores por tipo", description = "Retorna indicadores de um tipo específico")
    @ApiResponse(responseCode = "200", description = "Indicadores encontrados")
    public ResponseEntity<List<IndicatorResponse>> getIndicatorsByType(@PathVariable String type) {
        List<Indicator> indicators = indicatorService.getIndicatorsByType(type);
        return ResponseEntity.ok(
            indicators.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Buscar indicadores por categoria
     */
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar por categoria", description = "Retorna indicadores de uma categoria específica")
    @ApiResponse(responseCode = "200", description = "Indicadores encontrados")
    public ResponseEntity<List<IndicatorResponse>> getIndicatorsByCategory(
            @PathVariable UUID categoryId) {
        List<Indicator> indicators = indicatorService.getIndicatorsByCategory(categoryId);
        return ResponseEntity.ok(
            indicators.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Deletar indicador
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")
    @Operation(summary = "Deletar indicador", description = "Remove um indicador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Indicador deletado"),
        @ApiResponse(responseCode = "404", description = "Indicador não encontrado")
    })
    public ResponseEntity<Void> deleteIndicator(@PathVariable UUID id) {
        indicatorService.deleteIndicator(id);
        return ResponseEntity.noContent().build();
    }

    private IndicatorResponse toResponse(Indicator indicator) {
        return new IndicatorResponse(
            indicator.getId(),
            indicator.getIndicatorName(),
            indicator.getIndicatorType(),
            indicator.getDescription(),
            indicator.getValue(),
            indicator.getUnit(),
            indicator.getCategoryId(),
            indicator.getNeighborhood(),
            indicator.getPeriodStart(),
            indicator.getPeriodEnd(),
            indicator.getCalculatedAt()
        );
    }
}
