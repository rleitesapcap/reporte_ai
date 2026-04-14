package opus.social.app.reporteai.application.queryhandler;

import opus.social.app.reporteai.application.bus.QueryHandler;
import opus.social.app.reporteai.application.query.ListActiveUsersQuery;
import opus.social.app.reporteai.application.dto.UserResponse;
import opus.social.app.reporteai.infrastructure.persistence.repository.AuthUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler da Query ListActiveUsersQuery
 * CQRS Pattern - Otimizado para leitura de múltiplos usuários
 */
@Component
public class ListActiveUsersQueryHandler implements QueryHandler<ListActiveUsersQuery, List<UserResponse>> {
    private static final Logger logger = LoggerFactory.getLogger(ListActiveUsersQueryHandler.class);

    private final AuthUserRepository authUserRepository;

    public ListActiveUsersQueryHandler(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Override
    @Cacheable(value = "activeUsers", key = "#query.limit + '-' + #query.offset")
    public List<UserResponse> handle(ListActiveUsersQuery query) throws Exception {
        logger.debug("Executando query ListActiveUsers - limit: {}, offset: {}",
            query.getLimit(), query.getOffset());

        List<UserResponse> users = authUserRepository.findAllActiveUsers().stream()
            .skip(query.getOffset())
            .limit(query.getLimit())
            .map(UserResponse::from)
            .collect(Collectors.toList());

        logger.info("Query ListActiveUsers retornou {} usuários", users.size());
        return users;
    }
}
