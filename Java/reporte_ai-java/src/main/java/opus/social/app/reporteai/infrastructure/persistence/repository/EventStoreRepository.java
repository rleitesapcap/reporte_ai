package opus.social.app.reporteai.infrastructure.persistence.repository;

import opus.social.app.reporteai.domain.event.DomainEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Repository para persistência de eventos (Event Store)
 * Event Sourcing Pattern - Armazena todos os eventos imutavelmente
 *
 * Responsabilidades:
 * - Persistir eventos de domínio
 * - Recuperar eventos por agregado
 * - Prover rastreabilidade completa
 */
@Repository
public class EventStoreRepository {
    private static final Logger logger = LoggerFactory.getLogger(EventStoreRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public EventStoreRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Persiste um evento no Event Store
     */
    public void save(DomainEvent event) {
        try {
            String eventData = objectMapper.writeValueAsString(event);

            String sql = "INSERT INTO event_store " +
                "(event_id, aggregate_id, event_type, event_data, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";

            int result = jdbcTemplate.update(sql,
                event.getEventId().toString(),
                event.getAggregateId().toString(),
                event.getEventType(),
                eventData,
                LocalDateTime.now()
            );

            if (result > 0) {
                logger.info("Evento persistido: {} para agregado: {}",
                    event.getEventType(),
                    event.getAggregateId());
            }
        } catch (Exception ex) {
            logger.error("Erro ao persistir evento: {}", event.getEventType(), ex);
            throw new RuntimeException("Erro ao salvar evento no Event Store", ex);
        }
    }

    /**
     * Recupera todos os eventos para um agregado específico
     */
    public String getEventsForAggregate(UUID aggregateId) {
        try {
            String sql = "SELECT event_data FROM event_store " +
                "WHERE aggregate_id = ? " +
                "ORDER BY created_at ASC";

            return jdbcTemplate.queryForObject(sql,
                String.class,
                aggregateId.toString());
        } catch (Exception ex) {
            logger.warn("Nenhum evento encontrado para agregado: {}", aggregateId);
            return "[]";
        }
    }

    /**
     * Conta eventos para um agregado
     */
    public int countEventsForAggregate(UUID aggregateId) {
        String sql = "SELECT COUNT(*) FROM event_store WHERE aggregate_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, aggregateId.toString());
        return count != null ? count : 0;
    }

    /**
     * Script SQL para criar a tabela event_store
     * Execute isto no banco de dados manualmente ou via Flyway migration
     */
    public static String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS event_store (" +
            "  event_id UUID PRIMARY KEY DEFAULT gen_random_uuid()," +
            "  aggregate_id UUID NOT NULL," +
            "  event_type VARCHAR(255) NOT NULL," +
            "  event_data TEXT NOT NULL," +
            "  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "  INDEX idx_aggregate (aggregate_id)," +
            "  INDEX idx_created (created_at)" +
            ");";
    }
}
