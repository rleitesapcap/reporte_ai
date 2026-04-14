package opus.social.app.reporteai.application.bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Query Bus - Orquestrador Central de Queries
 * Padrão CQRS: Coordena execução de queries para seus handlers específicos
 *
 * Responsabilidades:
 * - Registrar handlers de queries
 * - Rotear queries para handlers corretos
 * - Executar com cache quando apropriado
 * - Tratamento centralizado de erros
 */
@Component
public class QueryBus {
    private static final Logger logger = LoggerFactory.getLogger(QueryBus.class);

    private final ApplicationContext applicationContext;
    private final Map<Class<?>, QueryHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    public QueryBus(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.registerHandlers();
    }

    /**
     * Descobre e registra todos os QueryHandlers disponíveis
     */
    private void registerHandlers() {
        Map<String, QueryHandler> beans = applicationContext.getBeansOfType(QueryHandler.class);

        for (QueryHandler<?, ?> handler : beans.values()) {
            Class<?> queryClass = extractQueryClass(handler);
            if (queryClass != null) {
                handlers.put(queryClass, handler);
                logger.info("Handler registrado: {} -> {}",
                    queryClass.getSimpleName(),
                    handler.getClass().getSimpleName());
            }
        }
    }

    /**
     * Extrai o tipo genérico de Query do handler
     */
    @SuppressWarnings("unchecked")
    private Class<?> extractQueryClass(QueryHandler<?, ?> handler) {
        Type[] types = handler.getClass().getGenericInterfaces();

        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                if (pt.getRawType().getTypeName().startsWith("opus.social.app.reporteai.application.bus.QueryHandler")) {
                    Type[] args = pt.getActualTypeArguments();
                    if (args.length > 0) {
                        return (Class<?>) args[0];
                    }
                }
            }
        }

        return null;
    }

    /**
     * Executa uma query através de seu handler registrado
     */
    @SuppressWarnings("unchecked")
    public <Q extends Query<R>, R> R execute(Q query) {
        if (query == null) {
            throw new IllegalArgumentException("Query não pode ser null");
        }

        Class<?> queryClass = query.getClass();
        QueryHandler<Q, R> handler = (QueryHandler<Q, R>) handlers.get(queryClass);

        if (handler == null) {
            String errorMsg = String.format(
                "Handler não registrado para query: %s. Handlers disponíveis: %s",
                queryClass.getSimpleName(),
                handlers.keySet()
            );
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        try {
            logger.debug("Executando query: {}", queryClass.getSimpleName());
            R result = handler.handle(query);
            logger.info("Query executada com sucesso: {}", queryClass.getSimpleName());
            return result;
        } catch (Exception ex) {
            logger.error("Erro ao executar query: {}", queryClass.getSimpleName(), ex);
            throw new RuntimeException("Erro ao executar query: " + ex.getMessage(), ex);
        }
    }

    /**
     * Retorna informações sobre handlers registrados (para monitoramento)
     */
    public Map<Class<?>, QueryHandler<?, ?>> getRegisteredHandlers() {
        return new ConcurrentHashMap<>(handlers);
    }
}
