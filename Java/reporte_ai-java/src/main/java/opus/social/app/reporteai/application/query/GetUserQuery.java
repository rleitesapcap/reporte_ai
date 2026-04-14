package opus.social.app.reporteai.application.query;

import opus.social.app.reporteai.application.bus.Query;
import opus.social.app.reporteai.application.dto.UserResponse;

/**
 * Query para obter dados de um usuário
 * CQRS Pattern - Representa uma intenção de leitura
 */
public class GetUserQuery implements Query<UserResponse> {
    private final String username;

    public GetUserQuery(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
