package opus.social.app.reporteai.application.query;

import opus.social.app.reporteai.application.bus.Query;
import opus.social.app.reporteai.application.dto.UserResponse;
import java.util.List;

/**
 * Query para listar usuários ativos
 * CQRS Pattern - Representa uma intenção de leitura complexa
 */
public class ListActiveUsersQuery implements Query<List<UserResponse>> {
    private final int limit;
    private final int offset;

    public ListActiveUsersQuery() {
        this(100, 0);
    }

    public ListActiveUsersQuery(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }
}
