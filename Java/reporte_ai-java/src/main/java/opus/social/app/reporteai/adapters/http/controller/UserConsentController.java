package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.UserConsentCreateRequest;
import opus.social.app.reporteai.application.dto.UserConsentResponse;
import opus.social.app.reporteai.application.service.UserConsentApplicationService;
import opus.social.app.reporteai.domain.entity.UserConsent;
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
 * Controller REST para Consentimento do Usuário
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/user-consents")
@Tag(name = "Consentimentos do Usuário", description = "Endpoints para gestão de consentimentos de usuários")
public class UserConsentController {

    private final UserConsentApplicationService consentService;

    public UserConsentController(UserConsentApplicationService consentService) {
        this.consentService = consentService;
    }

    /**
     * Registrar consentimento
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Registrar consentimento", description = "Registra um novo consentimento do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Consentimento registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserConsentResponse> recordConsent(
            @Valid @RequestBody UserConsentCreateRequest request) {
        UserConsent consent = consentService.recordConsent(
            request.getUserId(),
            request.getConsentType(),
            request.getAccepted(),
            request.getDocumentVersion(),
            request.getIpAddress()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(consent));
    }

    /**
     * Buscar consentimentos de um usuário
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("@authService.isUserOrAdmin(#userId)")
    @Operation(summary = "Buscar consentimentos", description = "Retorna todos os consentimentos de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consentimentos encontrados"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<List<UserConsentResponse>> getUserConsents(@PathVariable UUID userId) {
        List<UserConsent> consents = consentService.getUserConsents(userId);
        return ResponseEntity.ok(
            consents.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Buscar consentimentos por tipo
     */
    @GetMapping("/user/{userId}/type/{consentType}")
    @PreAuthorize("@authService.isUserOrAdmin(#userId)")
    @Operation(summary = "Buscar por tipo", description = "Retorna consentimentos de um tipo específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consentimentos encontrados"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<List<UserConsentResponse>> getUserConsentsByType(
            @PathVariable UUID userId,
            @PathVariable String consentType) {
        List<UserConsent> consents = consentService.getUserConsentsByType(userId, consentType);
        return ResponseEntity.ok(
            consents.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Deletar consentimento
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar consentimento", description = "Remove um consentimento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Consentimento deletado"),
        @ApiResponse(responseCode = "404", description = "Consentimento não encontrado")
    })
    public ResponseEntity<Void> deleteConsent(@PathVariable UUID id) {
        consentService.deleteConsent(id);
        return ResponseEntity.noContent().build();
    }

    private UserConsentResponse toResponse(UserConsent consent) {
        return new UserConsentResponse(
            consent.getId(),
            consent.getUserId(),
            consent.getConsentType(),
            consent.getAccepted(),
            consent.getConsentDate(),
            consent.getDocumentVersion(),
            consent.getIpAddress()
        );
    }
}
