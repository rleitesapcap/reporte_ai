package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.SystemSettingsCreateRequest;
import opus.social.app.reporteai.application.dto.SystemSettingsResponse;
import opus.social.app.reporteai.application.service.SystemSettingsApplicationService;
import opus.social.app.reporteai.domain.entity.SystemSettings;
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
 * Controller REST para Configurações do Sistema
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/system-settings")
@Tag(name = "Configurações do Sistema", description = "Endpoints para gestão de configurações do sistema")
public class SystemSettingsController {

    private final SystemSettingsApplicationService settingsService;

    public SystemSettingsController(SystemSettingsApplicationService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * Criar nova configuração
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar configuração", description = "Cria uma nova configuração do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Configuração criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<SystemSettingsResponse> saveSetting(
            @Valid @RequestBody SystemSettingsCreateRequest request) {
        SystemSettings setting = settingsService.saveSetting(
            request.getKey(),
            request.getValue(),
            request.getType(),
            request.getDescription()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(setting));
    }

    /**
     * Buscar configuração por chave
     */
    @GetMapping("/{key}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Buscar configuração", description = "Retorna uma configuração específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuração encontrada"),
        @ApiResponse(responseCode = "404", description = "Configuração não encontrada")
    })
    public ResponseEntity<SystemSettingsResponse> getSetting(@PathVariable String key) {
        SystemSettings setting = settingsService.getSetting(key);
        return ResponseEntity.ok(toResponse(setting));
    }

    /**
     * Listar todas as configurações
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar configurações", description = "Retorna todas as configurações do sistema")
    @ApiResponse(responseCode = "200", description = "Configurações encontradas")
    public ResponseEntity<List<SystemSettingsResponse>> getAllSettings() {
        List<SystemSettings> settings = settingsService.getAllSettings();
        return ResponseEntity.ok(
            settings.stream().map(this::toResponse).toList()
        );
    }

    /**
     * Atualizar configuração
     */
    @PutMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar configuração", description = "Atualiza o valor de uma configuração")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuração atualizada"),
        @ApiResponse(responseCode = "404", description = "Configuração não encontrada")
    })
    public ResponseEntity<SystemSettingsResponse> updateSetting(
            @PathVariable String key,
            @RequestParam String value) {
        SystemSettings setting = settingsService.updateSetting(key, value);
        return ResponseEntity.ok(toResponse(setting));
    }

    /**
     * Deletar configuração
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar configuração", description = "Remove uma configuração")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Configuração deletada"),
        @ApiResponse(responseCode = "404", description = "Configuração não encontrada")
    })
    public ResponseEntity<Void> deleteSetting(@PathVariable UUID id) {
        settingsService.deleteSetting(id);
        return ResponseEntity.noContent().build();
    }

    private SystemSettingsResponse toResponse(SystemSettings setting) {
        return new SystemSettingsResponse(
            setting.getId(),
            setting.getSettingKey(),
            setting.getSettingValue(),
            setting.getSettingType(),
            setting.getDescription(),
            setting.getUpdatedAt()
        );
    }
}
