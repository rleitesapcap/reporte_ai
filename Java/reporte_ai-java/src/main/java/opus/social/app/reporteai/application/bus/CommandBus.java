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
 * Command Bus - Orquestrador Central de Comandos
 * Padrão CQRS: Coordena execução de comandos para seus handlers específicos
 *
 * Responsabilidades:
 * - Registrar handlers de comandos
 * - Rotear comandos para handlers corretos
 * - Executar com transações e auditoria
 * - Tratamento centralizado de erros
 */
@Component
public class CommandBus {
    private static final Logger logger = LoggerFactory.getLogger(CommandBus.class);

    private final ApplicationContext applicationContext;
    private final Map<Class<?>, CommandHandler<?>> handlers = new ConcurrentHashMap<>();

    public CommandBus(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.registerHandlers();
    }

    /**
     * Descobre e registra todos os CommandHandlers disponíveis
     */
    private void registerHandlers() {
        Map<String, CommandHandler> beans = applicationContext.getBeansOfType(CommandHandler.class);

        for (CommandHandler<?> handler : beans.values()) {
            Class<?> commandClass = extractCommandClass(handler);
            if (commandClass != null) {
                handlers.put(commandClass, handler);
                logger.info("Handler registrado: {} -> {}",
                    commandClass.getSimpleName(),
                    handler.getClass().getSimpleName());
            }
        }
    }

    /**
     * Extrai o tipo genérico de Command do handler
     */
    @SuppressWarnings("unchecked")
    private Class<?> extractCommandClass(CommandHandler<?> handler) {
        Type[] types = handler.getClass().getGenericInterfaces();

        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                if (pt.getRawType() == CommandHandler.class) {
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
     * Executa um comando através de seu handler registrado
     */
    @SuppressWarnings("unchecked")
    public <C extends Command> void execute(C command) {
        if (command == null) {
            throw new IllegalArgumentException("Comando não pode ser null");
        }

        Class<?> commandClass = command.getClass();
        CommandHandler<C> handler = (CommandHandler<C>) handlers.get(commandClass);

        if (handler == null) {
            String errorMsg = String.format(
                "Handler não registrado para comando: %s. Handlers disponíveis: %s",
                commandClass.getSimpleName(),
                handlers.keySet()
            );
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        try {
            logger.debug("Executando comando: {}", commandClass.getSimpleName());
            handler.handle(command);
            logger.info("Comando executado com sucesso: {}", commandClass.getSimpleName());
        } catch (Exception ex) {
            logger.error("Erro ao executar comando: {}", commandClass.getSimpleName(), ex);
            throw new RuntimeException("Erro ao executar comando: " + ex.getMessage(), ex);
        }
    }

    /**
     * Retorna informações sobre handlers registrados (para monitoramento)
     */
    public Map<Class<?>, CommandHandler<?>> getRegisteredHandlers() {
        return new ConcurrentHashMap<>(handlers);
    }
}
