package opus.social.app.reporteai.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Serviço de auditoria para conformidade com LGPD
 * Registra todas as operações sensíveis para rastreamento e conformidade
 */
@Service
@Transactional
public class AuditLogApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogApplicationService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("audit");

    /**
     * Tipos de eventos de auditoria
     */
    public enum AuditEventType {
        USER_LOGIN("USER_LOGIN"),
        USER_LOGOUT("USER_LOGOUT"),
        USER_REGISTRATION("USER_REGISTRATION"),
        PASSWORD_CHANGE("PASSWORD_CHANGE"),
        PASSWORD_RESET("PASSWORD_RESET"),
        ROLE_ASSIGNMENT("ROLE_ASSIGNMENT"),
        DATA_ACCESS("DATA_ACCESS"),
        DATA_MODIFICATION("DATA_MODIFICATION"),
        DATA_DELETION("DATA_DELETION"),
        PERMISSION_CHANGE("PERMISSION_CHANGE"),
        SECURITY_INCIDENT("SECURITY_INCIDENT"),
        FAILED_LOGIN_ATTEMPT("FAILED_LOGIN_ATTEMPT"),
        ACCOUNT_LOCK("ACCOUNT_LOCK"),
        ACCOUNT_UNLOCK("ACCOUNT_UNLOCK"),
        CONSENT_UPDATE("CONSENT_UPDATE"),
        DATA_EXPORT("DATA_EXPORT");

        private final String description;

        AuditEventType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Registra um evento de auditoria
     */
    public void logAuditEvent(AuditEventType eventType, String resourceType, String resourceId, String details) {
        try {
            String userId = getCurrentUserId();
            String requestId = getOrGenerateRequestId();
            LocalDateTime timestamp = LocalDateTime.now(ZoneId.of("UTC"));

            Map<String, Object> auditEntry = new HashMap<>();
            auditEntry.put("timestamp", timestamp);
            auditEntry.put("eventType", eventType.getDescription());
            auditEntry.put("userId", userId);
            auditEntry.put("requestId", requestId);
            auditEntry.put("resourceType", resourceType);
            auditEntry.put("resourceId", resourceId);
            auditEntry.put("details", details);

            // Log estruturado para auditoria
            auditLogger.info(
                "AUDIT_EVENT | timestamp={} | eventType={} | userId={} | requestId={} | resourceType={} | resourceId={} | details={}",
                timestamp, eventType.getDescription(), userId, requestId, resourceType, resourceId, details
            );

        } catch (Exception ex) {
            logger.error("Erro ao registrar evento de auditoria", ex);
        }
    }

    /**
     * Registra login bem-sucedido
     */
    public void logSuccessfulLogin(String username) {
        logAuditEvent(
            AuditEventType.USER_LOGIN,
            "USER",
            username,
            "Login bem-sucedido"
        );
    }

    /**
     * Registra tentativa de login falhada
     */
    public void logFailedLoginAttempt(String username, String reason) {
        logAuditEvent(
            AuditEventType.FAILED_LOGIN_ATTEMPT,
            "USER",
            username,
            "Tentativa de login falhada: " + reason
        );
    }

    /**
     * Registra logout
     */
    public void logLogout(String username) {
        logAuditEvent(
            AuditEventType.USER_LOGOUT,
            "USER",
            username,
            "Logout realizado"
        );
    }

    /**
     * Registra registro de novo usuário
     */
    public void logUserRegistration(String username, String email) {
        logAuditEvent(
            AuditEventType.USER_REGISTRATION,
            "USER",
            username,
            "Novo usuário registrado: " + email
        );
    }

    /**
     * Registra mudança de senha
     */
    public void logPasswordChange(String username) {
        logAuditEvent(
            AuditEventType.PASSWORD_CHANGE,
            "USER",
            username,
            "Senha alterada com sucesso"
        );
    }

    /**
     * Registra incidente de segurança
     */
    public void logSecurityIncident(String incidentType, String description) {
        logAuditEvent(
            AuditEventType.SECURITY_INCIDENT,
            "SECURITY",
            "SYSTEM",
            incidentType + ": " + description
        );
    }

    /**
     * Registra acesso a dados sensíveis
     */
    public void logDataAccess(String resourceType, String resourceId, String details) {
        logAuditEvent(
            AuditEventType.DATA_ACCESS,
            resourceType,
            resourceId,
            "Acesso a dados: " + details
        );
    }

    /**
     * Registra modificação de dados
     */
    public void logDataModification(String resourceType, String resourceId, String changes) {
        logAuditEvent(
            AuditEventType.DATA_MODIFICATION,
            resourceType,
            resourceId,
            "Dados modificados: " + changes
        );
    }

    /**
     * Registra deleção de dados
     */
    public void logDataDeletion(String resourceType, String resourceId, String reason) {
        logAuditEvent(
            AuditEventType.DATA_DELETION,
            resourceType,
            resourceId,
            "Dados deletados: " + reason
        );
    }

    /**
     * Registra atualização de consentimento
     */
    public void logConsentUpdate(String userId, String consentType, boolean granted) {
        logAuditEvent(
            AuditEventType.CONSENT_UPDATE,
            "CONSENT",
            userId,
            "Consentimento " + consentType + " foi " + (granted ? "concedido" : "revogado")
        );
    }

    /**
     * Registra bloqueio de conta
     */
    public void logAccountLock(String username, String reason) {
        logAuditEvent(
            AuditEventType.ACCOUNT_LOCK,
            "USER",
            username,
            "Conta bloqueada: " + reason
        );
    }

    /**
     * Obtém o ID do usuário atual ou "ANONYMOUS" se não autenticado
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "ANONYMOUS";
    }

    /**
     * Obtém ou gera um ID de requisição para rastreamento
     */
    private String getOrGenerateRequestId() {
        String requestId = MDC.get("requestId");
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
        }
        return requestId;
    }
}
