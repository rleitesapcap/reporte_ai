package opus.social.app.reporteai.application.bus;

/**
 * Interface para handlers de comandos
 * Implementa padrão CQRS - Cada comando tem seu handler específico
 */
public interface CommandHandler<C extends Command> {
    /**
     * Executa o comando
     */
    void handle(C command) throws Exception;
}
