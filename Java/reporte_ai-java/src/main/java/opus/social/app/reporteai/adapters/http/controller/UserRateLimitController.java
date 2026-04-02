package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.UserRateLimitResponse;
import opus.social.app.reporteai.application.service.UserRateLimitApplicationService;
import opus.social.app.reporteai.domain.entity.UserRateLimit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Controller REST para Limite de Taxa de Usuário
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/user-rate-limits")
@Tag(name = "Limites de Taxa", description = "Endpoints para gestão de limites de taxa de usuários")
public class UserRateLimitController {

    private final UserRateLimitApplicationService rateLimitService;

    public UserRateLimitController(UserRateLimitApplicationService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    /**
     * Criar novo limite de taxa
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar limite", description = "Cria um novo limite de taxa para um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Limite criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<UserRateLimitResponse> createRateLimit(@RequestParam UUID userId) {
        UserRateLimit rateLimit = rateLimitService.createRateLimit(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(rateLimit));
    }

    /**
     * Buscar limite de taxa por usuário
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar limite", description = "Retorna o limite de taxa de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Limite encontrado"),
        @ApiResponse(responseCode = "404", description = "Limite não encontrado")
    })
    public ResponseEntity<UserRateLimitResponse> getRateLimitByUserId(@PathVariable UUID userId) {
        UserRateLimit rateLimit = rateLimitService.getRateLimitByUserId(userId);
        return ResponseEntity.ok(toResponse(rateLimit));
    }

    /**
     * Bloquear usuário
     */
    @PostMapping("/user/{userId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bloquear usuário", description = "Bloqueia um usuário até uma data específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário bloqueado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Limite não encontrado")
    })
    public ResponseEntity<UserRateLimitResponse> blockUser(
            @PathVariable UUID userId,
            @RequestParam LocalDateTime until) {
        rateLimitService.blockUser(userId, until);
        UserRateLimit rateLimit = rateLimitService.getRateLimitByUserId(userId);
        return ResponseEntity.ok(toResponse(rateLimit));
    }

    /**
     * Desbloquear usuário
     */
    @PostMapping("/user/{userId}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desbloquear usuário", description = "Remove o bloqueio de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário desbloqueado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Limite não encontrado")
    })
    public ResponseEntity<UserRateLimitResponse> unblockUser(@PathVariable UUID userId) {
        rateLimitService.unblockUser(userId);
        UserRateLimit rateLimit = rateLimitService.getRateLimitByUserId(userId);
        return ResponseEntity.ok(toResponse(rateLimit));
    }

    private UserRateLimitResponse toResponse(UserRateLimit rateLimit) {
        return new UserRateLimitResponse(
            rateLimit.getId(),
            rateLimit.getUserId(),
            rateLimit.getDailyLimit(),
            rateLimit.getHourlyLimit(),
            rateLimit.getDailyCount(),
            rateLimit.getHourlyCount(),
            rateLimit.getLastResetDaily(),
            rateLimit.getLastResetHourly(),
            rateLimit.getIsBlocked(),
            rateLimit.getBlockedUntil(),
            rateLimit.getCreatedAt()
        );
    }
}
