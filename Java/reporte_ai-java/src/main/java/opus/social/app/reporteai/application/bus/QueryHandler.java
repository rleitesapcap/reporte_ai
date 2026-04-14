package opus.social.app.reporteai.application.bus;

/**
 * Interface para handlers de queries
 * Implementa padrão CQRS - Cada query tem seu handler específico
 */
public interface QueryHandler<Q extends Query<R>, R> {
    /**
     * Executa a query e retorna resultado
     */
    R handle(Q query) throws Exception;
}
