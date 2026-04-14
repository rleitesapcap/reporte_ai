package opus.social.app.reporteai.application.listener;

import opus.social.app.reporteai.application.service.AuditLogApplicationService;
import opus.social.app.reporteai.domain.event.DomainEvent;
import opus.social.app.reporteai.domain.event.DomainEventListener;
import opus.social.app.reporteai.domain.event.UserLoginEvent;
import opus.social.app.reporteai.domain.event.UserPasswordChangedEvent;
import opus.social.app.reporteai.domain.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener que reage a eventos de domínio e registra em auditoria
 * Observer Pattern - Desacoplado do fluxo principal
 */
@Component
public class AuditLogEventListener implements DomainEventListener {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogEventListener.class);

    private final AuditLogApplicationService auditLogService;

    public AuditLogEventListener(AuditLogApplicationService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    @Async
    public void handle(DomainEvent event) throws Exception {
        logger.debug("AuditLogEventListener processando evento: {}", event.getEventType());

        if (event instanceof UserRegisteredEvent) {
            handleUserRegistered((UserRegisteredEvent) event);
        } else if (event instanceof UserPasswordChangedEvent) {
            handlePasswordChanged((UserPasswordChangedEvent) event);
        } else if (event instanceof UserLoginEvent) {
            handleUserLogin((UserLoginEvent) event);
        }
    }

    @Override
    public boolean canHandle(DomainEvent event) {
        return event instanceof UserRegisteredEvent
            || event instanceof UserPasswordChangedEvent
            || event instanceof UserLoginEvent;
    }

    private void handleUserRegistered(UserRegisteredEvent event) {
        logger.debug("Registrando auditoria de UserRegisteredEvent");
        auditLogService.logUserRegistration(event.getUsername(), event.getEmail());
    }

    private void handlePasswordChanged(UserPasswordChangedEvent event) {
        logger.debug("Registrando auditoria de UserPasswordChangedEvent");
        auditLogService.logPasswordChange(event.getUsername());
    }

    private void handleUserLogin(UserLoginEvent event) {
        logger.debug("Registrando auditoria de UserLoginEvent");
        auditLogService.logSuccessfulLogin(event.getUsername());
    }
}
