package opus.social.app.reporteai.application.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Circuit Breaker Service - Proteção contra falhas em cascata
 * Resilience4j Pattern
 *
 * Responsabilidades:
 * - Monitorar falhas em serviços externos
 * - Interromper chamadas quando limite é atingido
 * - Permitir recuperação gradual (HALF_OPEN)
 * - Fornecer fallback automático
 */
@Service
public class CircuitBreakerService {
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerService.class);

    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();
    private final CircuitBreakerRegistry registry;

    public CircuitBreakerService() {
        this.registry = CircuitBreakerRegistry.ofDefaults();
    }

    /**
     * Cria ou obtém um CircuitBreaker para o serviço especificado
     */
    public CircuitBreaker getOrCreateCircuitBreaker(String serviceName) {
        return circuitBreakers.computeIfAbsent(serviceName, key -> {
            CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                // Abre o circuito se 50% das chamadas falham
                .failureRateThreshold(50.0f)
                // Abre se chamadas lentas (> 2s) excedem 50%
                .slowCallRateThreshold(50.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                // Número de chamadas a testar no estado HALF_OPEN
                .permittedNumberOfCallsInHalfOpenState(3)
                // Transição automática de OPEN para HALF_OPEN após 30s
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                // Tipos de exceção a registrar como falha
                .recordExceptions(
                    Exception.class,
                    java.io.IOException.class,
                    java.util.concurrent.TimeoutException.class
                )
                // Exceções de negócio não contam como falha técnica
                .ignoreExceptions(
                    opus.social.app.reporteai.domain.exception.BusinessException.class
                )
                .build();

            CircuitBreaker cb = registry.circuitBreaker(serviceName, config);
            logger.info("CircuitBreaker criado para serviço: {}", serviceName);
            return cb;
        });
    }

    /**
     * Executa uma operação com proteção de circuit breaker
     */
    public <T> T executeWithCircuitBreaker(String serviceName, Supplier<T> operation) {
        CircuitBreaker cb = getOrCreateCircuitBreaker(serviceName);

        try {
            logger.debug("Executando operação com circuit breaker: {}", serviceName);
            return cb.executeSupplier(operation);
        } catch (Exception ex) {
            logger.error("Erro ao executar operação com circuit breaker: {}", serviceName, ex);

            // Se o circuito está aberto
            if (cb.getState().toString().equals("OPEN")) {
                logger.warn("Circuit breaker aberto para serviço: {}", serviceName);
                throw new RuntimeException("Serviço indisponível: " + serviceName, ex);
            }

            throw ex;
        }
    }

    /**
     * Executa uma operação com fallback
     */
    public <T> T executeWithFallback(String serviceName, Supplier<T> operation, T fallbackValue) {
        try {
            return executeWithCircuitBreaker(serviceName, operation);
        } catch (Exception ex) {
            logger.warn("Usando fallback para serviço: {}. Erro: {}", serviceName, ex.getMessage());
            return fallbackValue;
        }
    }

    /**
     * Obtém status do circuit breaker
     */
    public String getCircuitBreakerStatus(String serviceName) {
        CircuitBreaker cb = circuitBreakers.get(serviceName);
        if (cb == null) {
            return "NOT_INITIALIZED";
        }
        return cb.getState().toString();
    }

    /**
     * Reseta um circuit breaker para estado inicial
     */
    public void resetCircuitBreaker(String serviceName) {
        CircuitBreaker cb = circuitBreakers.get(serviceName);
        if (cb != null) {
            cb.reset();
            logger.info("Circuit breaker resetado: {}", serviceName);
        }
    }

    /**
     * Lista todos os circuit breakers e seus status
     */
    public Map<String, String> getAllCircuitBreakerStatus() {
        Map<String, String> status = new ConcurrentHashMap<>();
        circuitBreakers.forEach((name, cb) ->
            status.put(name, cb.getState().toString())
        );
        return status;
    }
}
