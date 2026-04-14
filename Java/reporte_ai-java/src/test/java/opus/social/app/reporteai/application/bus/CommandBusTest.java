package opus.social.app.reporteai.application.bus;

import opus.social.app.reporteai.application.command.RegisterUserCommand;
import opus.social.app.reporteai.application.commandhandler.RegisterUserCommandHandler;
import opus.social.app.reporteai.domain.specification.StrongPasswordSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * CommandBus Testes Unitários
 *
 * Valida:
 * - Roteamento automático de comandos para handlers
 * - Execução correta de comandos
 * - Tratamento de exceções
 * - Validações de comando
 */
@DisplayName("CommandBus Pattern Tests")
class CommandBusTest {

    private CommandBus commandBus;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private RegisterUserCommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandBus = new CommandBus(applicationContext);
    }

    @Test
    @DisplayName("Deve executar comando registrado com sucesso")
    void testExecuteCommandSuccess() throws Exception {
        // Arrange
        RegisterUserCommand command = RegisterUserCommand.builder()
            .username("testuser")
            .email("test@example.com")
            .password("SecurePassword123!@")
            .fullName("Test User")
            .build();

        when(applicationContext.getBean(RegisterUserCommandHandler.class))
            .thenReturn(commandHandler);

        // Act
        commandBus.execute(command);

        // Assert
        verify(commandHandler, times(1)).handle(command);
    }

    @Test
    @DisplayName("Deve lançar exceção para comando sem handler")
    void testExecuteCommandWithoutHandler() {
        // Arrange
        RegisterUserCommand command = RegisterUserCommand.builder()
            .username("testuser")
            .email("test@example.com")
            .password("SecurePassword123!@")
            .fullName("Test User")
            .build();

        when(applicationContext.getBean(any(Class.class)))
            .thenThrow(new RuntimeException("No handler found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> commandBus.execute(command));
    }

    @Test
    @DisplayName("Deve propagar exceção do handler")
    void testExecuteCommandHandlerException() throws Exception {
        // Arrange
        RegisterUserCommand command = RegisterUserCommand.builder()
            .username("testuser")
            .email("test@example.com")
            .password("WeakPass")
            .fullName("Test User")
            .build();

        when(applicationContext.getBean(RegisterUserCommandHandler.class))
            .thenReturn(commandHandler);
        doThrow(new IllegalArgumentException("Senha fraca"))
            .when(commandHandler).handle(command);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandBus.execute(command));
    }

    @Test
    @DisplayName("Deve validar comando antes de executar")
    void testCommandValidation() {
        // Arrange
        RegisterUserCommand command = RegisterUserCommand.builder()
            .username(null)  // username inválido
            .email("test@example.com")
            .password("SecurePassword123!@")
            .fullName("Test User")
            .build();

        // Act & Assert
        assertThrows(Exception.class, () -> commandBus.execute(command));
    }
}
