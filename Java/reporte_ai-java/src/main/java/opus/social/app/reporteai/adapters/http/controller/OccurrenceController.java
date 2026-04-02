package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.OccurrenceCreateRequest;
import opus.social.app.reporteai.application.dto.OccurrenceResponse;
import opus.social.app.reporteai.application.service.OccurrenceApplicationService;
import opus.social.app.reporteai.domain.entity.Occurrence;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/occurrences")
@Tag(name = "Occurrences", description = "Occurrence management endpoints")
public class OccurrenceController {
    private final OccurrenceApplicationService occurrenceService;

    public OccurrenceController(OccurrenceApplicationService occurrenceService) {
        this.occurrenceService = occurrenceService;
    }

    @PostMapping
    @Operation(summary = "Create a new occurrence")
    public ResponseEntity<OccurrenceResponse> createOccurrence(@Valid @RequestBody OccurrenceCreateRequest request) {
        Occurrence occurrence = occurrenceService.createOccurrence(request.getUserId(),
            request.getCategoryId(), request.getSubCategoryId(), null,
            request.getDescription(), request.getNeighborhood(), request.getReferencePoint(),
            request.getLatitude(), request.getLongitude(), request.getSeverity(),
            request.getFrequency());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(occurrence));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get occurrence by ID")
    public ResponseEntity<OccurrenceResponse> getOccurrenceById(@PathVariable UUID id) {
        Occurrence occurrence = occurrenceService.getOccurrenceById(id);
        return ResponseEntity.ok(toResponse(occurrence));
    }

    @GetMapping("/protocol/{protocolId}")
    @Operation(summary = "Get occurrence by protocol ID")
    public ResponseEntity<OccurrenceResponse> getOccurrenceByProtocol(@PathVariable String protocolId) {
        Occurrence occurrence = occurrenceService.getOccurrenceByProtocolId(protocolId);
        return ResponseEntity.ok(toResponse(occurrence));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get occurrences by user ID")
    public ResponseEntity<List<OccurrenceResponse>> getOccurrencesByUser(@PathVariable UUID userId) {
        List<Occurrence> occurrences = occurrenceService.getOccurrencesByUserId(userId);
        return ResponseEntity.ok(occurrences.stream().map(this::toResponse).toList());
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get occurrences by category")
    public ResponseEntity<List<OccurrenceResponse>> getOccurrencesByCategory(@PathVariable UUID categoryId) {
        List<Occurrence> occurrences = occurrenceService.getOccurrencesByCategory(categoryId);
        return ResponseEntity.ok(occurrences.stream().map(this::toResponse).toList());
    }

    @GetMapping("/neighborhood/{neighborhood}")
    @Operation(summary = "Get occurrences by neighborhood")
    public ResponseEntity<List<OccurrenceResponse>> getOccurrencesByNeighborhood(@PathVariable String neighborhood) {
        List<Occurrence> occurrences = occurrenceService.getOccurrencesByNeighborhood(neighborhood);
        return ResponseEntity.ok(occurrences.stream().map(this::toResponse).toList());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get occurrences by status")
    public ResponseEntity<List<OccurrenceResponse>> getOccurrencesByStatus(@PathVariable String status) {
        List<Occurrence> occurrences = occurrenceService.getOccurrencesByStatus(status);
        return ResponseEntity.ok(occurrences.stream().map(this::toResponse).toList());
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent occurrences")
    public ResponseEntity<List<OccurrenceResponse>> getRecentOccurrences(@RequestParam(defaultValue = "10") int limit) {
        List<Occurrence> occurrences = occurrenceService.getRecentOccurrences(limit);
        return ResponseEntity.ok(occurrences.stream().map(this::toResponse).toList());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update occurrence status")
    public ResponseEntity<OccurrenceResponse> updateStatus(@PathVariable UUID id,
            @RequestParam String status) {
        Occurrence occurrence = occurrenceService.updateOccurrenceStatus(id, status);
        return ResponseEntity.ok(toResponse(occurrence));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete occurrence")
    public ResponseEntity<Void> deleteOccurrence(@PathVariable UUID id) {
        occurrenceService.deleteOccurrence(id);
        return ResponseEntity.noContent().build();
    }

    private OccurrenceResponse toResponse(Occurrence occurrence) {
        return new OccurrenceResponse(occurrence.getId(), occurrence.getProtocolId(),
            occurrence.getCategoryId(), occurrence.getDescription(),
            occurrence.getNeighborhood(), occurrence.getSeverity(), occurrence.getFrequency(),
            occurrence.getPriorityScore(), occurrence.getStatus(), occurrence.getPhotoCount(),
            occurrence.getCreatedAt(), occurrence.getUpdatedAt());
    }
}
