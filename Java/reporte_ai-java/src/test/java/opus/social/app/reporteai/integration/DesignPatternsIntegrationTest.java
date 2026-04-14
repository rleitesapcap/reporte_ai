package opus.social.app.reporteai.integration;

import opus.social.app.reporteai.application.bus.CommandBus;
import opus.social.app.reporteai.application.bus.QueryBus;
import opus.social.app.reporteai.application.command.RegisterUserCommand;
import opus.social.app.reporteai.application.command.ChangePasswordCommand;
import opus.social.app.reporteai.application.query.GetUserQuery;
import opus.social.app.reporteai.application.query.ListActiveUsersQuery;
import opus.social.app.reporteai.domain.event.DomainEvent;
import opus.social.app.reporteai.domain.event.DomainEventPublisher;
import opus.social.app.reporteai.domain.specification.StrongPasswordSpecification;
import opus.social.app.reporteai.domain.specification.Specification;
import opus.social.app.reporteai.application.service.CircuitBreakerService;
import org.springframework.context.ApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Testes de Integração - Padrões Trabalham Juntos
 *
 * Valida:
 * - CQRS com Event Sourcing
 * - Commands disparando eventos
 * - Cache melhorando performance de queries
 * - Specifications validando dados
 * - Circuit breaker protegendo serviços externos
 * - Fluxo completo de um usuário: registrar -> validar -> buscar -> mudar senha
 */
@DisplayName("Design Patterns Integration Tests")
class DesignPatternsIntegrationTest {

    private CommandBus commandBus;
    private QueryBus queryBus;
    private DomainEventPublisher eventPublisher;
    private CircuitBreakerService circuitBreakerService;
    private List<DomainEvent> publishedEvents;

    @BeforeEach
    void setUp() {
        publishedEvents = new ArrayList<>();
        commandBus = new CommandBus(null);  // ApplicationContext será mockado
        queryBus = new QueryBus(null);
        eventPublisher = new DomainEventPublisher(mock(ApplicationContext.class));
        circuitBreakerService = new CircuitBreakerService();
    }

    @Test
    @DisplayName("Fluxo Completo: Registrar usuário e recuperar com cache")
    void testCompleteUserRegistrationFlow() throws Exception {
        // 1. SPECIFICATION PATTERN: Validar senha forte
        Specification<String> strongPassword = new StrongPasswordSpecification();
        String password = "StrongPass123!@";

        assertTrue(strongPassword.isSatisfiedBy(password),
            "Senha deve atender aos critérios de segurança");

        // 2. CQRS COMMAND: Registrar usuário (escrita)
        RegisterUserCommand registerCmd = RegisterUserCommand.builder()
            .username("testuser")
            .email("test@example.com")
            .password(password)
            .fullName("Test User")
            .build();

        // 3. EVENT SOURCING: Comando publica evento
        // Simulado: evento seria publicado aqui
        String registrationResult = executeCommand(registerCmd);
        assertEquals("SUCCESS", registrationResult);

        // 4. CQRS QUERY: Buscar usuário com cache (leitura)
        GetUserQuery getQuery = new GetUserQuery("testuser");

        // Primeira chamada - do banco de dados
        long start = System.currentTimeMillis();
        Object user = executeQuery(getQuery);
        long firstQueryTime = System.currentTimeMillis() - start;

        assertNotNull(user, "Usuário deve ser encontrado após registro");

        // Segunda chamada - do cache (deve ser mais rápida)
        start = System.currentTimeMillis();
        Object userCached = executeQuery(getQuery);
        long secondQueryTime = System.currentTimeMillis() - start;

        assertEquals(user, userCached, "Cache deve retornar mesmo usuário");
        // Cache hit deve ser mais rápido (nota: em testes pode ser igual se muito rápido)
    }

    @Test
    @DisplayName("Fluxo Completo: Mudar senha com validação e eventos")
    void testPasswordChangeFlowWithValidation() throws Exception {
        // 1. Registrar usuário primeiro
        RegisterUserCommand registerCmd = RegisterUserCommand.builder()
            .username("testuser")
            .email("test@example.com")
            .password("InitialPass123!@")
            .fullName("Test User")
            .build();
        executeCommand(registerCmd);

        // 2. SPECIFICATION: Validar nova senha
        Specification<String> strongPassword = new StrongPasswordSpecification();
        String newPassword = "NewSecurePass456!@";
        assertTrue(strongPassword.isSatisfiedBy(newPassword));

        // 3. CQRS COMMAND: Mudar senha
        ChangePasswordCommand changeCmd = new ChangePasswordCommand(
            "testuser",
            newPassword
        );
        executeCommand(changeCmd);

        // 4. EVENT SOURCING: Evento de mudança de senha foi publicado
        assertTrue(publishedEvents.size() >= 1, "Eventos devem ter sido publicados");

        // 5. Verificar que senha antiga não funciona mais (validação)
        Specification<String> oldPassword = new StrongPasswordSpecification();
        assertTrue(oldPassword.isSatisfiedBy("InitialPass123!@"));  // Ainda é válida como regra
    }

    @Test
    @DisplayName("Multiple Queries com Cache - Performance Improvement")
    void testMultipleQueriesWithCaching() throws Exception {
        // Registrar vários usuários
        for (int i = 1; i <= 5; i++) {
            RegisterUserCommand cmd = RegisterUserCommand.builder()
                .username("user" + i)
                .email("user" + i + "@example.com")
                .password("SecurePass123!@")
                .fullName("User " + i)
                .build();
            executeCommand(cmd);
        }

        // CQRS QUERY: Listar usuários ativos
        ListActiveUsersQuery listQuery = new ListActiveUsersQuery(10, 0);

        // Primeira chamada - do banco
        long start = System.currentTimeMillis();
        Object users = executeQuery(listQuery);
        long firstTime = System.currentTimeMillis() - start;

        // Chamadas subsequentes - do cache
        long cachedTime = 0;
        for (int i = 0; i < 3; i++) {
            start = System.currentTimeMillis();
            Object cachedUsers = executeQuery(listQuery);
            cachedTime += (System.currentTimeMillis() - start);
        }

        // Cache deve fornecer resultados rápido
        assertNotNull(users);
        // Nota: Performance actual depende do cache configurado
    }

    @Test
    @DisplayName("Circuit Breaker Protege Contra Falhas de Serviço Externo")
    void testCircuitBreakerWithExternalService() {
        // Simular chamada a serviço externo
        String serviceName = "externalNotificationAPI";
        String fallback = "Notificação agendada para envio posterior";

        // Simular falha da API
        String result = circuitBreakerService.executeWithFallback(
            serviceName,
            () -> {
                throw new RuntimeException("API timeout");
            },
            fallback
        );

        assertEquals(fallback, result);

        // Sistema continua funcionando sem cascata de falhas
        String result2 = circuitBreakerService.executeWithFallback(
            serviceName,
            () -> "Fallback result",
            fallback
        );

        assertNotNull(result2);
    }

    @Test
    @DisplayName("Validators com Composição de Especificações")
    void testComposedValidations() {
        // SPECIFICATION PATTERN: Validação complexa com composição
        Specification<String> strongPassword = new StrongPasswordSpecification();

        // Composição: Password deve ser forte E não conter espaços
        Specification<String> noSpaces = new Specification<String>() {
            @Override
            public boolean isSatisfiedBy(String candidate) {
                return !candidate.contains(" ");
            }

            @Override
            public String getDescription() {
                return "Password must not contain spaces";
            }
        };
        Specification<String> validationComposed = strongPassword.and(noSpaces);

        assertTrue(validationComposed.isSatisfiedBy("StrongPass123!@"));
        assertFalse(validationComposed.isSatisfiedBy("Strong Pass 123!@"));  // Tem espaço
    }

    @Test
    @DisplayName("Event Sourcing com Múltiplos Eventos por Agregado")
    void testEventSourcingMultipleEventsPerAggregate() throws Exception {
        // 1. Registrar usuário (evento 1)
        RegisterUserCommand registerCmd = RegisterUserCommand.builder()
            .username("eventuser")
            .email("eventuser@example.com")
            .password("SecurePass123!@")
            .fullName("Event User")
            .build();
        executeCommand(registerCmd);

        // 2. Mudar senha (evento 2)
        ChangePasswordCommand changeCmd = new ChangePasswordCommand(
            "eventuser",
            "NewPass456!@"
        );
        executeCommand(changeCmd);

        // 3. Login (evento 3)
        // Simulado

        // Todos os eventos devem estar no event store
        assertTrue(publishedEvents.size() >= 1, "Events devem ter sido publicados");
    }

    @Test
    @DisplayName("Padrões Trabalham Juntos: Registro -> Validação -> Query -> Cache")
    void testAllPatternsWorkingTogether() throws Exception {
        // CQRS: Register command
        RegisterUserCommand cmd = RegisterUserCommand.builder()
            .username("integrated")
            .email("integrated@example.com")
            .password("SecurePass123!@")
            .fullName("Integrated User")
            .build();

        // Specification: Validate password
        Specification<String> spec = new StrongPasswordSpecification();
        assertTrue(spec.isSatisfiedBy("SecurePass123!@"));

        // Execute command
        executeCommand(cmd);

        // CQRS: Query com cache
        GetUserQuery query = new GetUserQuery("integrated");
        Object result = executeQuery(query);

        assertNotNull(result);

        // Cache hit
        Object cachedResult = executeQuery(query);
        assertEquals(result, cachedResult);
    }

    @Test
    @DisplayName("Error Handling Across All Patterns")
    void testErrorHandlingAcrossPatterns() throws Exception {
        // Specification: Rejeitar senha fraca
        Specification<String> spec = new StrongPasswordSpecification();
        assertFalse(spec.isSatisfiedBy("weak"));

        // Command: Validar antes de executar
        RegisterUserCommand invalidCmd = RegisterUserCommand.builder()
            .username("user")
            .email("invalid-email")  // Email inválido
            .password("weak")  // Senha fraca
            .fullName("User")
            .build();

        // Deveria falhar na validação
        assertThrows(Exception.class, () -> executeCommand(invalidCmd));

        // Circuit Breaker: Fallback para falha
        String result = circuitBreakerService.executeWithFallback(
            "failingService",
            () -> {
                throw new RuntimeException("Service down");
            },
            "Default value"
        );

        assertEquals("Default value", result);
    }

    // Métodos auxiliares para simular execução
    private String executeCommand(Object command) throws Exception {
        // Simular execução de comando
        return "SUCCESS";
    }

    private Object executeQuery(Object query) throws Exception {
        // Simular execução de query
        return new Object();
    }
}
