package opus.social.app.reporteai.application.bus;

import opus.social.app.reporteai.application.query.GetUserQuery;
import opus.social.app.reporteai.application.query.ListActiveUsersQuery;
import opus.social.app.reporteai.application.queryhandler.GetUserQueryHandler;
import opus.social.app.reporteai.application.queryhandler.ListActiveUsersQueryHandler;
import opus.social.app.reporteai.application.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * QueryBus Testes Unitários
 *
 * Valida:
 * - Roteamento de queries para handlers
 * - Retorno correto de dados
 * - Caching de resultados
 * - Tratamento de exceções em queries
 */
@DisplayName("QueryBus Pattern Tests")
class QueryBusTest {

    private QueryBus queryBus;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private GetUserQueryHandler getUserHandler;

    @Mock
    private ListActiveUsersQueryHandler listActiveHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        queryBus = new QueryBus(applicationContext);
    }

    @Test
    @DisplayName("Deve executar GetUserQuery e retornar usuário")
    void testExecuteGetUserQuery() throws Exception {
        // Arrange
        GetUserQuery query = new GetUserQuery("testuser");
        UserResponse expectedUser = new UserResponse(null, null, "Test User", "test@example.com", null, null, null, null, null);

        when(applicationContext.getBean(GetUserQueryHandler.class))
            .thenReturn(getUserHandler);
        when(getUserHandler.handle(query))
            .thenReturn(expectedUser);

        // Act
        UserResponse result = queryBus.execute(query);

        // Assert
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        verify(getUserHandler, times(1)).handle(query);
    }

    @Test
    @DisplayName("Deve executar ListActiveUsersQuery e retornar lista paginada")
    void testExecuteListActiveUsersQuery() throws Exception {
        // Arrange
        ListActiveUsersQuery query = new ListActiveUsersQuery(10, 0);
        List<UserResponse> expectedUsers = List.of(
            new UserResponse(null, null, "User 1", "user1@example.com", null, null, null, null, null),
            new UserResponse(null, null, "User 2", "user2@example.com", null, null, null, null, null)
        );

        when(applicationContext.getBean(ListActiveUsersQueryHandler.class))
            .thenReturn(listActiveHandler);
        when(listActiveHandler.handle(query))
            .thenReturn(expectedUsers);

        // Act
        List<UserResponse> result = queryBus.execute(query);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(listActiveHandler, times(1)).handle(query);
    }

    @Test
    @DisplayName("Deve lançar exceção para query sem handler")
    void testExecuteQueryWithoutHandler() {
        // Arrange
        GetUserQuery query = new GetUserQuery("testuser");

        when(applicationContext.getBean(any(Class.class)))
            .thenThrow(new RuntimeException("No handler found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> queryBus.execute(query));
    }

    @Test
    @DisplayName("Deve retornar null quando usuário não encontrado")
    void testQueryUserNotFound() throws Exception {
        // Arrange
        GetUserQuery query = new GetUserQuery("nonexistent");

        when(applicationContext.getBean(GetUserQueryHandler.class))
            .thenReturn(getUserHandler);
        when(getUserHandler.handle(query))
            .thenReturn(null);

        // Act
        UserResponse result = queryBus.execute(query);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum usuário ativo")
    void testQueryEmptyList() throws Exception {
        // Arrange
        ListActiveUsersQuery query = new ListActiveUsersQuery(10, 0);

        when(applicationContext.getBean(ListActiveUsersQueryHandler.class))
            .thenReturn(listActiveHandler);
        when(listActiveHandler.handle(query))
            .thenReturn(List.of());

        // Act
        List<UserResponse> result = queryBus.execute(query);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve propagar exceção do handler")
    void testQueryHandlerException() throws Exception {
        // Arrange
        GetUserQuery query = new GetUserQuery("testuser");

        when(applicationContext.getBean(GetUserQueryHandler.class))
            .thenReturn(getUserHandler);
        when(getUserHandler.handle(query))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> queryBus.execute(query));
    }
}
