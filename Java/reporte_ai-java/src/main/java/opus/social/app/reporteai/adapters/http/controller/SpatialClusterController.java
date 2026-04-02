package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.SpatialClusterResponse;
import opus.social.app.reporteai.application.service.SpatialClusterApplicationService;
import opus.social.app.reporteai.domain.entity.SpatialCluster;
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
 * Controller REST para Clusters Espaciais
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/spatial-clusters")
@Tag(name = "Clusters Espaciais", description = "Endpoints para gestão de clusters espaciais")
public class SpatialClusterController {

    private final SpatialClusterApplicationService clusterService;

    public SpatialClusterController(SpatialClusterApplicationService clusterService) {
        this.clusterService = clusterService;
    }

    /**
     * Criar novo cluster
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")
    @Operation(summary = "Criar cluster", description = "Cria um novo cluster espacial")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cluster criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<SpatialClusterResponse> createCluster(
            @RequestParam String name,
            @RequestParam String neighborhood,
            @RequestParam BigDecimal centerLat,
            @RequestParam BigDecimal centerLon,
            @RequestParam BigDecimal radius) {
        SpatialCluster cluster = clusterService.createCluster(
            name, neighborhood, centerLat, centerLon, radius
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(cluster));
    }

    /**
     * Buscar clusters por bairro
     */
    @GetMapping("/neighborhood/{neighborhood}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar por bairro", description = "Retorna clusters de um bairro específico")
    @ApiResponse(responseCode = "200", description = "Clusters encontrados")
    public ResponseEntity<List<SpatialClusterResponse>> getClustersByNeighborhood(
            @PathVariable String neighborhood) {
        List<SpatialCluster> clusters = clusterService.getClustersByNeighborhood(neighborhood);
        return ResponseEntity.ok(
            clusters.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Deletar cluster
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar cluster", description = "Remove um cluster espacial")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cluster deletado"),
        @ApiResponse(responseCode = "404", description = "Cluster não encontrado")
    })
    public ResponseEntity<Void> deleteCluster(@PathVariable UUID id) {
        clusterService.deleteCluster(id);
        return ResponseEntity.noContent().build();
    }

    private SpatialClusterResponse toResponse(SpatialCluster cluster) {
        return new SpatialClusterResponse(
            cluster.getId(),
            cluster.getClusterName(),
            cluster.getNeighborhood(),
            cluster.getCenterLatitude(),
            cluster.getCenterLongitude(),
            cluster.getRadius(),
            cluster.getOccurrenceCount(),
            cluster.getAverageTrustScore(),
            cluster.getMinimumDistance(),
            cluster.getMaximumDistance(),
            cluster.getCreatedAt(),
            cluster.getUpdatedAt()
        );
    }
}
