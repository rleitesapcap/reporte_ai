package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.NotificationCreateRequest;
import opus.social.app.reporteai.application.dto.NotificationResponse;
import opus.social.app.reporteai.application.service.NotificationApplicationService;
import opus.social.app.reporteai.domain.entity.Notification;
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
 * Controller REST para Notificações
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notificações", description = "Endpoints para gestão de notificações")
public class NotificationController {

    private final NotificationApplicationService notificationService;

    public NotificationController(NotificationApplicationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Enviar nova notificação
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('NOTIFICATION_SENDER')")
    @Operation(summary = "Enviar notificação", description = "Envia uma nova notificação para um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notificação enviada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody NotificationCreateRequest request) {
        Notification notification = notificationService.sendNotification(
            request.getUserId(),
            request.getNotificationType(),
            request.getTitle(),
            request.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(notification));
    }

    /**
     * Buscar notificações de um usuário
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("@authService.isUserOrAdmin(#userId)")
    @Operation(summary = "Buscar notificações do usuário", description = "Retorna todas as notificações de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificações recuperadas"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @PathVariable UUID userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(
            notifications.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Buscar notificações não lidas de um usuário
     */
    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("@authService.isUserOrAdmin(#userId)")
    @Operation(summary = "Buscar notificações não lidas", description = "Retorna notificações não lidas de um usuário")
    @ApiResponse(responseCode = "200", description = "Notificações não lidas")
    public ResponseEntity<List<NotificationResponse>> getUserUnreadNotifications(
            @PathVariable UUID userId) {
        List<Notification> notifications = notificationService.getUserUnreadNotifications(userId);
        return ResponseEntity.ok(
            notifications.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Marcar notificação como lida
     */
    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Marcar notificação como lida", description = "Marca uma notificação como lida")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificação marcada como lida"),
        @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(toResponse(notification));
    }

    /**
     * Deletar notificação
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Deletar notificação", description = "Remove uma notificação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Notificação deletada"),
        @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getUserId(),
            notification.getOccurrenceId(),
            notification.getNotificationType(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getSentAt(),
            notification.getReadAt(),
            notification.getIsRead()
        );
    }
}
