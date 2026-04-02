package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.OccurrenceImageCreateRequest;
import opus.social.app.reporteai.application.dto.OccurrenceImageResponse;
import opus.social.app.reporteai.application.service.OccurrenceImageApplicationService;
import opus.social.app.reporteai.domain.entity.OccurrenceImage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Imagens de Ocorrência
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/occurrence-images")
@Tag(name = "Imagens de Ocorrência", description = "Endpoints para gestão de imagens de ocorrência")
public class OccurrenceImageController {

    private final OccurrenceImageApplicationService imageService;

    public OccurrenceImageController(OccurrenceImageApplicationService imageService) {
        this.imageService = imageService;
    }

    /**
     * Fazer upload de imagem
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Upload de imagem", description = "Faz upload de uma nova imagem para uma ocorrência")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Imagem enviada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Ocorrência não encontrada")
    })
    public ResponseEntity<OccurrenceImageResponse> uploadImage(
            @Valid @RequestBody OccurrenceImageCreateRequest request) {
        OccurrenceImage image = imageService.uploadImage(
            request.getOccurrenceId(),
            request.getS3Url(),
            request.getS3Key(),
            request.getImageSize(),
            request.getImageFormat()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(image));
    }

    /**
     * Buscar imagem por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar imagem por ID", description = "Retorna os dados de uma imagem específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagem encontrada"),
        @ApiResponse(responseCode = "404", description = "Imagem não encontrada")
    })
    public ResponseEntity<OccurrenceImageResponse> getImageById(@PathVariable UUID id) {
        OccurrenceImage image = imageService.getImageById(id);
        return ResponseEntity.ok(toResponse(image));
    }

    /**
     * Buscar imagens de uma ocorrência
     */
    @GetMapping("/occurrence/{occurrenceId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar imagens da ocorrência", description = "Retorna todas as imagens de uma ocorrência")
    @ApiResponse(responseCode = "200", description = "Imagens encontradas")
    public ResponseEntity<List<OccurrenceImageResponse>> getImagesByOccurrence(
            @PathVariable UUID occurrenceId) {
        List<OccurrenceImage> images = imageService.getImagesByOccurrence(occurrenceId);
        return ResponseEntity.ok(
            images.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Marcar imagem como processada
     */
    @PutMapping("/{id}/mark-processed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")
    @Operation(summary = "Marcar como processada", description = "Marca uma imagem como processada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagem marcada como processada"),
        @ApiResponse(responseCode = "404", description = "Imagem não encontrada")
    })
    public ResponseEntity<OccurrenceImageResponse> markImageAsProcessed(@PathVariable UUID id) {
        OccurrenceImage image = imageService.markImageAsProcessed(id);
        return ResponseEntity.ok(toResponse(image));
    }

    /**
     * Deletar imagem
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar imagem", description = "Remove uma imagem")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Imagem deletada"),
        @ApiResponse(responseCode = "404", description = "Imagem não encontrada")
    })
    public ResponseEntity<Void> deleteImage(@PathVariable UUID id) {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletar todas as imagens de uma ocorrência
     */
    @DeleteMapping("/occurrence/{occurrenceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar imagens da ocorrência", description = "Remove todas as imagens de uma ocorrência")
    @ApiResponse(responseCode = "204", description = "Imagens deletadas")
    public ResponseEntity<Void> deleteImagesByOccurrence(@PathVariable UUID occurrenceId) {
        imageService.deleteImagesByOccurrence(occurrenceId);
        return ResponseEntity.noContent().build();
    }

    private OccurrenceImageResponse toResponse(OccurrenceImage image) {
        return new OccurrenceImageResponse(
            image.getId(),
            image.getOccurrenceId(),
            image.getS3Url(),
            image.getS3Key(),
            image.getImageSize(),
            image.getImageFormat(),
            image.getUploadedAt(),
            image.getProcessed()
        );
    }
}
