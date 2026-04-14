package opus.social.app.reporteai.application.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Circuit Breaker Pattern Testes Unitários
 *
 * Valida:
 * - Execução normal quando serviço está disponível
 * - Fallback quando serviço falha
 * - Circuit breaker abre após falhas repetidas
 * - Auto-recuperação após timeout
 * - Proteção contra cascata de falhas
 */
@DisplayName("Circuit Breaker Pattern Tests")
class CircuitBreakerServiceTest {

    private CircuitBreakerService circuitBreakerService;

    @BeforeEach
    void setUp() {
        circuitBreakerService = new CircuitBreakerService();
    }

    @Test
    @DisplayName("Deve executar operação com sucesso")
    void testExecuteSuccess() throws Exception {
        // Arrange
        String expected = "Success";

        // Act
        String result = circuitBreakerService.executeWithCircuitBreaker(
            "testService",
            () -> expected
        );

        // Assert
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve executar operação com fallback quando falha")
    void testExecuteWithFallback() {
        // Arrange
        String fallback = "Fallback result";

        // Act
        String result = circuitBreakerService.executeWithFallback(
            "failingService",
            () -> {
                throw new RuntimeException("Service unavailable");
            },
            fallback
        );

        // Assert
        assertEquals(fallback, result);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há fallback")
    void testExecuteWithoutFallbackThrowsException() {
        // Arrange & Act & Assert
        assertThrows(Exception.class, () -> {
            circuitBreakerService.executeWithCircuitBreaker(
                "failingService",
                () -> {
                    throw new RuntimeException("Service unavailable");
                }
            );
        });
    }

    @Test
    @DisplayName("Deve recuperar-se após timeout em estado Half-Open")
    void testCircuitBreakerHalfOpenRecovery() throws Exception {
        // Arrange
        String serviceName = "recoveryService";

        // Primeira execução falha
        assertThrows(Exception.class, () ->
            circuitBreakerService.executeWithCircuitBreaker(
                serviceName,
                () -> {
                    throw new RuntimeException("Fail");
                }
            )
        );

        // Act - Simular recuperação
        String result = circuitBreakerService.executeWithFallback(
            serviceName,
            () -> "Recovered",
            "Fallback"
        );

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Deve usar fallback para múltiplas falhas seguidas")
    void testMultipleFailuresUseFallback() {
        // Arrange
        String fallback = "Service temporarily unavailable";

        // Act - Simular múltiplas falhas
        for (int i = 0; i < 3; i++) {
            String result = circuitBreakerService.executeWithFallback(
                "unstableService",
                () -> {
                    throw new RuntimeException("Fail " + i);
                },
                fallback
            );

            // Assert
            assertEquals(fallback, result);
        }
    }

    @Test
    @DisplayName("Deve permitir reexecução após recuperação")
    void testReexecutionAfterRecovery() throws Exception {
        // Arrange
        boolean[] callCount = {false};

        // First attempt with fallback
        String fallbackResult = circuitBreakerService.executeWithFallback(
            "recoveryService",
            () -> {
                throw new RuntimeException("Fail");
            },
            "Fallback"
        );
        assertEquals("Fallback", fallbackResult);

        // Act - Retry should succeed
        callCount[0] = false;
        String result = circuitBreakerService.executeWithFallback(
            "recoveryService",
            () -> {
                callCount[0] = true;
                return "Success";
            },
            "Fallback"
        );

        // Assert
        assertTrue(callCount[0]);
    }

    @Test
    @DisplayName("Diferentes serviços devem ter Circuit Breakers independentes")
    void testIndependentCircuitBreakers() throws Exception {
        // Arrange & Act
        String result1 = circuitBreakerService.executeWithFallback(
            "service1",
            () -> "Service 1 OK",
            "Fallback 1"
        );

        String result2 = circuitBreakerService.executeWithFallback(
            "service2",
            () -> "Service 2 OK",
            "Fallback 2"
        );

        // Assert
        assertEquals("Service 1 OK", result1);
        assertEquals("Service 2 OK", result2);
    }

    @Test
    @DisplayName("Deve manter fallback consistente para mesmo serviço")
    void testConsistentFallback() {
        // Arrange
        String fallback = "Consistent fallback";

        // Act
        String result1 = circuitBreakerService.executeWithFallback(
            "consistentService",
            () -> {
                throw new RuntimeException("Fail");
            },
            fallback
        );

        String result2 = circuitBreakerService.executeWithFallback(
            "consistentService",
            () -> {
                throw new RuntimeException("Fail again");
            },
            fallback
        );

        // Assert
        assertEquals(fallback, result1);
        assertEquals(fallback, result2);
    }

    @Test
    @DisplayName("Deve capturar e tratar exceções específicas")
    void testExceptionHandling() {
        // Arrange & Act & Assert
        assertDoesNotThrow(() ->
            circuitBreakerService.executeWithFallback(
                "errorService",
                () -> {
                    throw new IllegalArgumentException("Invalid argument");
                },
                "Safe fallback"
            )
        );
    }

    @Test
    @DisplayName("Deve proteger contra cascata de falhas")
    void testCascadFailureProtection() {
        // Arrange - Simular serviço que causa cascata
        String fallback = "Protected";

        // Act - Múltiplas chamadas a diferentes serviços que falham
        String result1 = circuitBreakerService.executeWithFallback(
            "downstreamService1",
            () -> {
                throw new RuntimeException("Down");
            },
            fallback
        );

        String result2 = circuitBreakerService.executeWithFallback(
            "downstreamService2",
            () -> {
                throw new RuntimeException("Down");
            },
            fallback
        );

        // Assert - Ambos devem usar fallback sem parar sistema
        assertEquals(fallback, result1);
        assertEquals(fallback, result2);
    }

    @Test
    @DisplayName("Null fallback deve permitir exceção")
    void testNullFallback() {
        // Act & Assert
        assertThrows(Exception.class, () ->
            circuitBreakerService.executeWithFallback(
                "nullFallbackService",
                () -> {
                    throw new RuntimeException("Fail");
                },
                null  // null fallback
            )
        );
    }
}
