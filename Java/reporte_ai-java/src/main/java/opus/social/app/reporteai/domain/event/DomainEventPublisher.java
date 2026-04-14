package opus.social.app.reporteai.domain.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Publisher Central de Eventos de Domínio
 * Observer Pattern + Event Sourcing
 *
 * Responsabilidades:
 * - Registrar listeners de eventos
 * - Publicar eventos para todos os listeners interessados
 * - Executar listeners de forma assincronapara não bloquear fluxo
 * - Tratamento de erros em listeners
 */
@Component
public class DomainEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(DomainEventPublisher.class);

    private final List<DomainEventListener> listeners = new CopyOnWriteArrayList<>();
    private final ApplicationContext applicationContext;

    public DomainEventPublisher(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.registerListeners();
    }

    /**
     * Descobre e registra todos os DomainEventListeners disponíveis
     */
    private void registerListeners() {
        applicationContext.getBeansOfType(DomainEventListener.class)
            .values()
            .forEach(listener -> {
                listeners.add(listener);
                logger.info("Listener registrado: {}", listener.getClass().getSimpleName());
            });
    }

    /**
     * Publica um evento para todos os listeners registrados
     * Execução assincronam para não bloquear o fluxo principal
     */
    @Async
    public void publish(DomainEvent event) {
        logger.info("Publicando evento de domínio: {} [{}]", event.getEventType(), event.getAggregateId());

        for (DomainEventListener listener : listeners) {
            if (listener.canHandle(event)) {
                try {
                    logger.debug("Processando evento {} com listener {}",
                        event.getEventType(),
                        listener.getClass().getSimpleName());
                    listener.handle(event);
                    logger.debug("Evento {} processado com sucesso por {}",
                        event.getEventType(),
                        listener.getClass().getSimpleName());
                } catch (Exception ex) {
                    logger.error("Erro ao processar evento {} em listener {}",
                        event.getEventType(),
                        listener.getClass().getSimpleName(), ex);
                    // Continua processando outros listeners mesmo com erro
                }
            }
        }

        logger.info("Publicação do evento {} concluída", event.getEventType());
    }

    /**
     * Registra um novo listener manualmente (além da auto-descoberta)
     */
    public void subscribe(DomainEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            logger.info("Listener subscrito manualmente: {}", listener.getClass().getSimpleName());
        }
    }

    /**
     * Remove um listener
     */
    public void unsubscribe(DomainEventListener listener) {
        if (listeners.remove(listener)) {
            logger.info("Listener removido: {}", listener.getClass().getSimpleName());
        }
    }

    /**
     * Retorna lista de listeners registrados
     */
    public List<DomainEventListener> getListeners() {
        return new CopyOnWriteArrayList<>(listeners);
    }
}
