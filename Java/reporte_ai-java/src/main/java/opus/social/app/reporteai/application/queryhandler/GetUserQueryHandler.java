package opus.social.app.reporteai.application.queryhandler;

import opus.social.app.reporteai.application.bus.QueryHandler;
import opus.social.app.reporteai.application.query.GetUserQuery;
import opus.social.app.reporteai.application.dto.UserResponse;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthUserJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.AuthUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Handler da Query GetUserQuery
 * CQRS Pattern - Responsável pela execução otimizada de leitura
 *
 * Características:
 * - Cacheable para otimizar leituras repetidas
 * - Apenas leitura, sem modificação de estado
 * - Pode ser escalado independentemente de comandos
 */
@Component
public class GetUserQueryHandler implements QueryHandler<GetUserQuery, UserResponse> {
    private static final Logger logger = LoggerFactory.getLogger(GetUserQueryHandler.class);

    private final AuthUserRepository authUserRepository;

    public GetUserQueryHandler(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Override
    @Cacheable(value = "users", key = "#query.username")
    public UserResponse handle(GetUserQuery query) throws Exception {
        logger.debug("Executando query GetUser para username: {}", query.getUsername());

        AuthUserJpaEntity user = authUserRepository.findByUsernameWithRoles(query.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + query.getUsername()));

        UserResponse response = UserResponse.from(user);
        logger.info("User query executada com sucesso: {}", query.getUsername());

        return response;
    }
}
