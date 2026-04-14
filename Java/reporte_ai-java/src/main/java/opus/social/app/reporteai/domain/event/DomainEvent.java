package opus.social.app.reporteai.domain.event;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Classe base para todos os eventos de domínio
 * Event Sourcing Pattern - Registra todas as mudanças de estado como eventos imutáveis
 *
 * Características:
 * - Imutável após criação
 * - Rastreado com timestamp UTC
 * - Identificado univocamente
 * - Ligado ao agregado que gerou o evento
 */
public abstract class DomainEvent {
    private final UUID aggregateId;
    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final String eventType;

    protected DomainEvent(UUID aggregateId) {
        this.aggregateId = aggregateId;
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now(ZoneId.of("UTC"));
        this.eventType = this.getClass().getSimpleName();
    }

    // ===== Getters =====
    public UUID getAggregateId() {
        return aggregateId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public String getEventType() {
        return eventType;
    }

    /**
     * Retorna descrição legível do evento para auditoria
     */
    public abstract String getDescription();

    @Override
    public String toString() {
        return "DomainEvent{" +
            "eventType='" + eventType + '\'' +
            ", eventId=" + eventId +
            ", aggregateId=" + aggregateId +
            ", occurredAt=" + occurredAt +
            '}';
    }
}
