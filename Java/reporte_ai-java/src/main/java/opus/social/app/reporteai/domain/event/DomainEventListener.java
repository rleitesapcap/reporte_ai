package opus.social.app.reporteai.domain.event;

/**
 * Interface para listeners de eventos de domínio
 * Observer Pattern - Reage a eventos sem acoplamento
 *
 * Implementadores podem:
 * - Registrar em auditoria
 * - Enviar notificações
 * - Atualizar projeções (CQRS)
 * - Disparar ações assincronas
 */
public interface DomainEventListener {
    /**
     * Processa um evento de domínio
     */
    void handle(DomainEvent event) throws Exception;

    /**
     * Indica se este listener pode processar este tipo de evento
     */
    boolean canHandle(DomainEvent event);
}
