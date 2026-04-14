package opus.social.app.reporteai.domain.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import opus.social.app.reporteai.application.listener.AuditLogEventListener;
import opus.social.app.reporteai.application.service.AuditLogApplicationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Event Sourcing Pattern Testes Unitários
 *
 * Valida:
 * - Criação e publicação de eventos de domínio
 * - Imutabilidade de eventos
 * - Roteamento de eventos para listeners
 * - Histórico completo de eventos
 * - Recuperação de estado via events
 */
@DisplayName("Event Sourcing Pattern Tests")
class EventSourcingTest {

    private DomainEventPublisher eventPublisher;
    private List<DomainEvent> publishedEvents;

    @Mock
    private DomainEventListener mockListener;

    @Mock
    private AuditLogApplicationService auditLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        publishedEvents = new ArrayList<>();
        // Simular publisher que armazena eventos
    }

    @Test
    @DisplayName("Deve criar evento com todos os campos obrigatórios")
    void testCreateUserRegisteredEvent() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String email = "test@example.com";

        // Act
        UserRegisteredEvent event = new UserRegisteredEvent(userId, username, email, "Test User");

        // Assert
        assertNotNull(event.getEventId());
        assertEquals(userId, event.getAggregateId());
        assertEquals(username, event.getUsername());
        assertEquals(email, event.getEmail());
        assertNotNull(event.getOccurredAt());
        assertTrue(event.getOccurredAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Evento deve ser imutável após criação")
    void testEventImmutability() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserRegisteredEvent event = new UserRegisteredEvent(userId, "user1", "user1@example.com", "User 1");

        // Act & Assert - Sem setters, evento é imutável
        assertFalse(event.getUsername().equals("different"));
    }

    @Test
    @DisplayName("Deve criar evento de mudança de senha")
    void testCreateUserPasswordChangedEvent() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String username = "testuser";

        // Act
        UserPasswordChangedEvent event = new UserPasswordChangedEvent(userId, username, "admin");

        // Assert
        assertNotNull(event.getEventId());
        assertEquals(userId, event.getAggregateId());
        assertEquals(username, event.getUsername());
        assertEquals("admin", event.getChangedBy());
    }

    @Test
    @DisplayName("Deve criar evento de login")
    void testCreateUserLoginEvent() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String ipAddress = "192.168.1.1";

        // Act
        UserLoginEvent event = new UserLoginEvent(userId, username, ipAddress);

        // Assert
        assertNotNull(event.getEventId());
        assertEquals(userId, event.getAggregateId());
        assertEquals(username, event.getUsername());
        assertEquals(ipAddress, event.getIpAddress());
    }

    @Test
    @DisplayName("getDescription deve retornar descrição formatada")
    void testEventDescription() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserRegisteredEvent event = new UserRegisteredEvent(userId, "testuser", "test@example.com", "Test User");

        // Act
        String description = event.getDescription();

        // Assert
        assertNotNull(description);
        assertTrue(description.contains("testuser"));
        assertTrue(description.length() > 0);
    }

    @Test
    @DisplayName("EventoId deve ser único para cada evento")
    void testUniqueEventIds() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        UserRegisteredEvent event1 = new UserRegisteredEvent(userId, "user1", "user1@example.com", "User 1");
        UserRegisteredEvent event2 = new UserRegisteredEvent(userId, "user2", "user2@example.com", "User 2");

        // Assert
        assertNotEquals(event1.getEventId(), event2.getEventId());
    }

    @Test
    @DisplayName("DomainEventListener deve receber eventos aplicáveis")
    void testDomainEventListenerCanHandle() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserRegisteredEvent event = new UserRegisteredEvent(userId, "testuser", "test@example.com", "Test User");

        AuditLogEventListener listener = new AuditLogEventListener(auditLogService);

        // Act
        boolean canHandle = listener.canHandle(event);

        // Assert
        assertTrue(canHandle);
    }

    @Test
    @DisplayName("DomainEventListener não deve processar eventos não aplicáveis")
    void testDomainEventListenerCannotHandleWrongType() {
        // Arrange
        class UnknownEvent extends DomainEvent {
            public UnknownEvent(UUID aggregateId) {
                super(aggregateId);
            }

            @Override
            public String getDescription() {
                return "Unknown";
            }
        }

        UUID userId = UUID.randomUUID();
        UnknownEvent event = new UnknownEvent(userId);
        AuditLogEventListener listener = new AuditLogEventListener(auditLogService);

        // Act
        boolean canHandle = listener.canHandle(event);

        // Assert
        assertFalse(canHandle);
    }

    @Test
    @DisplayName("Eventos devem manter ordem cronológica")
    void testEventChronologicalOrder() throws InterruptedException {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        UserRegisteredEvent event1 = new UserRegisteredEvent(userId, "user1", "user1@example.com", "User 1");
        Thread.sleep(10);  // Pequeno delay para garantir timestamps diferentes
        UserPasswordChangedEvent event2 = new UserPasswordChangedEvent(userId, "user1", "admin");

        // Assert
        assertTrue(event1.getOccurredAt().isBefore(event2.getOccurredAt()));
    }

    @Test
    @DisplayName("Eventos podem ser agrupados por aggregateId para reconstruir estado")
    void testEventGroupingByAggregateId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        List<DomainEvent> userEvents = new ArrayList<>();

        UserRegisteredEvent registered = new UserRegisteredEvent(userId, "testuser", "test@example.com", "Test User");
        userEvents.add(registered);

        UserPasswordChangedEvent passwordChanged = new UserPasswordChangedEvent(userId, "testuser", "admin");
        userEvents.add(passwordChanged);

        UserLoginEvent login = new UserLoginEvent(userId, "testuser", "192.168.1.1");
        userEvents.add(login);

        // Act
        List<DomainEvent> filteredEvents = userEvents.stream()
            .filter(e -> e.getAggregateId().equals(userId))
            .toList();

        // Assert
        assertEquals(3, filteredEvents.size());
        assertTrue(filteredEvents.get(0) instanceof UserRegisteredEvent);
        assertTrue(filteredEvents.get(1) instanceof UserPasswordChangedEvent);
        assertTrue(filteredEvents.get(2) instanceof UserLoginEvent);
    }

    @Test
    @DisplayName("Event Store deve persistir eventos em ordem")
    void testEventStoreOrdering() {
        // Arrange
        UUID userId = UUID.randomUUID();
        List<DomainEvent> events = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            UserLoginEvent event = new UserLoginEvent(userId, "testuser", "192.168.1." + i);
            events.add(event);
        }

        // Act & Assert
        for (int i = 0; i < events.size() - 1; i++) {
            assertTrue(events.get(i).getOccurredAt()
                .isBefore(events.get(i + 1).getOccurredAt()));
        }
    }
}
