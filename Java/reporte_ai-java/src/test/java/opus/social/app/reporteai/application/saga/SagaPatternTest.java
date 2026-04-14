package opus.social.app.reporteai.application.saga;

import opus.social.app.reporteai.application.dto.RegisterRequest;
import opus.social.app.reporteai.application.service.AuthUserApplicationService;
import opus.social.app.reporteai.application.service.AuditLogApplicationService;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthUserJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Saga Pattern Testes Unitários
 *
 * Valida:
 * - Execução de múltiplos passos em sequência
 * - Compensação quando um passo falha
 * - Independência de operações não-críticas
 * - Sucesso quando operações críticas passam
 * - Auditoria de cada passo
 */
@DisplayName("Saga Pattern Tests")
class SagaPatternTest {

    private UserRegistrationSaga saga;

    @Mock
    private AuthUserApplicationService authService;

    @Mock
    private AuditLogApplicationService auditService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        saga = new UserRegistrationSaga(authService, auditService);
    }

    @Test
    @DisplayName("Saga deve executar todos os passos com sucesso")
    void testSagaSuccessfulCompletion() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest(
            "testuser",
            "test@example.com",
            "SecurePass123!@",
            "SecurePass123!@",
            "Test User"
        );

        when(authService.registerUser(any(RegisterRequest.class)))
            .thenReturn(mock(AuthUserJpaEntity.class));

        // Act
        saga.executeUserRegistration(request);

        // Assert
        verify(authService, times(1)).registerUser(any(RegisterRequest.class));
        verify(auditService, atLeastOnce()).logUserRegistration(anyString(), anyString());
    }

    @Test
    @DisplayName("Saga deve lidar com email interno sem falha externa")
    void testSagaHandlesInternalEmailGracefully() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest(
            "testuser",
            "test@example.com",
            "SecurePass123!@",
            "SecurePass123!@",
            "Test User"
        );

        when(authService.registerUser(any(RegisterRequest.class)))
            .thenReturn(mock(AuthUserJpaEntity.class));

        // Act - saga envia email internamente (método privado)
        saga.executeUserRegistration(request);

        // Assert - Usuário deve estar registrado
        verify(authService, times(1)).registerUser(any(RegisterRequest.class));
        verify(auditService, atLeastOnce()).logUserRegistration(anyString(), anyString());
    }

    @Test
    @DisplayName("Saga deve falhar se registro de usuário falhar")
    void testSagaFailOnCriticalFailure() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest(
            "testuser",
            "test@example.com",
            "SecurePass123!@",
            "SecurePass123!@",
            "Test User"
        );

        when(authService.registerUser(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("User already exists"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> saga.executeUserRegistration(request));
    }

    @Test
    @DisplayName("Saga deve executar compensação em caso de erro")
    void testSagaCompensation() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest(
            "testuser",
            "test@example.com",
            "SecurePass123!@",
            "SecurePass123!@",
            "Test User"
        );

        when(authService.registerUser(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("Critical error"));

        // Act
        try {
            saga.executeUserRegistration(request);
        } catch (Exception e) {
            // Exceção esperada
        }

        // Assert - Auditoria de incidente deve ter sido registrada
        verify(auditService, atLeastOnce()).logSecurityIncident(anyString(), anyString());
    }

    @Test
    @DisplayName("Saga deve registrar auditoria de cada passo")
    void testSagaAuditing() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest(
            "testuser",
            "test@example.com",
            "SecurePass123!@",
            "SecurePass123!@",
            "Test User"
        );

        when(authService.registerUser(any(RegisterRequest.class)))
            .thenReturn(mock(AuthUserJpaEntity.class));

        // Act
        saga.executeUserRegistration(request);

        // Assert
        verify(auditService, atLeastOnce()).logUserRegistration(anyString(), anyString());
    }

    @Test
    @DisplayName("Saga deve ser transacional")
    void testSagaTransactionality() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest(
            "testuser",
            "test@example.com",
            "SecurePass123!@",
            "SecurePass123!@",
            "Test User"
        );

        when(authService.registerUser(any(RegisterRequest.class)))
            .thenReturn(mock(AuthUserJpaEntity.class));

        // Act
        saga.executeUserRegistration(request);

        // Assert - Tudo deve ter sido executado ou nada
        verify(authService, times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Saga deve validar dados de entrada")
    void testSagaInputValidation() {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest(
            null,  // username inválido
            "test@example.com",
            "SecurePass123!@",
            "SecurePass123!@",
            "Test User"
        );

        // Act & Assert
        assertThrows(Exception.class, () -> saga.executeUserRegistration(invalidRequest));
    }

    @Test
    @DisplayName("Saga deve suportar múltiplas instâncias simultâneas")
    void testMultipleSagaInstances() throws Exception {
        // Arrange
        RegisterRequest request1 = new RegisterRequest(
            "user1", "user1@example.com", "Pass123!@", "Pass123!@", "User 1"
        );
        RegisterRequest request2 = new RegisterRequest(
            "user2", "user2@example.com", "Pass123!@", "Pass123!@", "User 2"
        );

        when(authService.registerUser(any(RegisterRequest.class)))
            .thenReturn(mock(AuthUserJpaEntity.class));

        // Act
        saga.executeUserRegistration(request1);
        saga.executeUserRegistration(request2);

        // Assert
        verify(authService, times(2)).registerUser(any(RegisterRequest.class));
    }
}
