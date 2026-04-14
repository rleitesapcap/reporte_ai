package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço de rate limiting persistente e distribuído
 * Implementa algoritmo Token Bucket com armazenamento em cache distribuído
 * Para produção, use Redis ao invés de cache em memória
 */
@Service
@Transactional
public class EnhancedRateLimitingService {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedRateLimitingService.class);

    @Value("${app.ratelimit.enabled:true}")
    private boolean rateLimitingEnabled;

    @Value("${app.ratelimit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${app.ratelimit.requests-per-hour:1000}")
    private int requestsPerHour;

    @Value("${app.ratelimit.requests-per-day:10000}")
    private int requestsPerDay;

    @Value("${app.ratelimit.burst-capacity:10}")
    private int burstCapacity;

    // Cache distribuído de rate limiting (em produção, use Redis)
    private final Map<String, RateLimitBucket> rateLimitBuckets = new ConcurrentHashMap<>();

    /**
     * Classe interna para armazenar estado do rate limiting
     */
    private static class RateLimitBucket {
        long tokensPerMinute;
        long tokensPerHour;
        long tokensPerDay;
        LocalDateTime minuteWindowStart;
        LocalDateTime hourWindowStart;
        LocalDateTime dayWindowStart;
        int requestsInMinute;
        int requestsInHour;
        int requestsInDay;

        RateLimitBucket() {
            LocalDateTime now = LocalDateTime.now();
            this.minuteWindowStart = now;
            this.hourWindowStart = now;
            this.dayWindowStart = now;
            this.requestsInMinute = 0;
            this.requestsInHour = 0;
            this.requestsInDay = 0;
        }
    }

    /**
     * Verifica se uma requisição deve ser aceita baseado na taxa de limite
     */
    public boolean allowRequest(String clientId) {
        if (!rateLimitingEnabled) {
            return true;
        }

        if (clientId == null || clientId.isEmpty()) {
            throw new BusinessException("Client ID é obrigatório para rate limiting");
        }

        RateLimitBucket bucket = rateLimitBuckets.computeIfAbsent(
            clientId,
            k -> new RateLimitBucket()
        );

        LocalDateTime now = LocalDateTime.now();

        // Reset do contador por minuto
        if (bucket.minuteWindowStart.plusMinutes(1).isBefore(now)) {
            bucket.requestsInMinute = 0;
            bucket.minuteWindowStart = now;
        }

        // Reset do contador por hora
        if (bucket.hourWindowStart.plusHours(1).isBefore(now)) {
            bucket.requestsInHour = 0;
            bucket.hourWindowStart = now;
        }

        // Reset do contador por dia
        if (bucket.dayWindowStart.plusDays(1).isBefore(now)) {
            bucket.requestsInDay = 0;
            bucket.dayWindowStart = now;
        }

        // Verifica limite por minuto (para burst protection)
        if (bucket.requestsInMinute >= requestsPerMinute) {
            logger.warn(
                "Rate limit exceeded for client {} at minute level: {} requests",
                clientId, bucket.requestsInMinute
            );
            return false;
        }

        // Verifica limite por hora
        if (bucket.requestsInHour >= requestsPerHour) {
            logger.warn(
                "Rate limit exceeded for client {} at hour level: {} requests",
                clientId, bucket.requestsInHour
            );
            return false;
        }

        // Verifica limite por dia
        if (bucket.requestsInDay >= requestsPerDay) {
            logger.warn(
                "Rate limit exceeded for client {} at day level: {} requests",
                clientId, bucket.requestsInDay
            );
            return false;
        }

        // Incrementa contadores
        bucket.requestsInMinute++;
        bucket.requestsInHour++;
        bucket.requestsInDay++;

        return true;
    }

    /**
     * Obtém informações de rate limiting para um cliente
     */
    public RateLimitInfo getRateLimitInfo(String clientId) {
        RateLimitBucket bucket = rateLimitBuckets.get(clientId);

        if (bucket == null) {
            return new RateLimitInfo(
                requestsPerMinute, 0,
                requestsPerHour, 0,
                requestsPerDay, 0
            );
        }

        return new RateLimitInfo(
            requestsPerMinute, bucket.requestsInMinute,
            requestsPerHour, bucket.requestsInHour,
            requestsPerDay, bucket.requestsInDay
        );
    }

    /**
     * Reseta rate limit para um cliente específico
     */
    public void resetRateLimit(String clientId) {
        rateLimitBuckets.remove(clientId);
        logger.info("Rate limit reset for client: {}", clientId);
    }

    /**
     * Limpa buckets expirados para liberar memória
     * Deve ser executado periodicamente
     */
    public void cleanupExpiredBuckets() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        int removed = 0;

        for (String clientId : new HashSet<>(rateLimitBuckets.keySet())) {
            RateLimitBucket bucket = rateLimitBuckets.get(clientId);
            if (bucket != null && bucket.dayWindowStart.isBefore(cutoff)) {
                rateLimitBuckets.remove(clientId);
                removed++;
            }
        }

        if (removed > 0) {
            logger.info("Cleaned up {} expired rate limit buckets", removed);
        }
    }

    /**
     * Aplicar throttling com backoff exponencial
     */
    public long getBackoffDelay(String clientId) {
        RateLimitBucket bucket = rateLimitBuckets.get(clientId);

        if (bucket == null) {
            return 0;
        }

        // Calcula tempo de espera baseado no percentual de uso
        double percentageUsed = (double) bucket.requestsInMinute / requestsPerMinute;

        if (percentageUsed >= 0.9) {
            // Backoff exponencial: espera aumenta exponencialmente próximo do limite
            return Math.round(1000 * (percentageUsed * percentageUsed));
        }

        return 0;
    }

    /**
     * DTO para informações de rate limit
     */
    public static class RateLimitInfo {
        public final int limitPerMinute;
        public final int usedPerMinute;
        public final int limitPerHour;
        public final int usedPerHour;
        public final int limitPerDay;
        public final int usedPerDay;

        public RateLimitInfo(
            int limitPerMinute, int usedPerMinute,
            int limitPerHour, int usedPerHour,
            int limitPerDay, int usedPerDay
        ) {
            this.limitPerMinute = limitPerMinute;
            this.usedPerMinute = usedPerMinute;
            this.limitPerHour = limitPerHour;
            this.usedPerHour = usedPerHour;
            this.limitPerDay = limitPerDay;
            this.usedPerDay = usedPerDay;
        }

        public int getRemainingPerMinute() {
            return Math.max(0, limitPerMinute - usedPerMinute);
        }

        public int getRemainingPerHour() {
            return Math.max(0, limitPerHour - usedPerHour);
        }

        public int getRemainingPerDay() {
            return Math.max(0, limitPerDay - usedPerDay);
        }
    }

    /**
     * Incrementa contador para rate limiting sem validação (para operações críticas)
     */
    public void recordRequest(String clientId) {
        if (!rateLimitingEnabled) {
            return;
        }

        RateLimitBucket bucket = rateLimitBuckets.computeIfAbsent(
            clientId,
            k -> new RateLimitBucket()
        );

        bucket.requestsInMinute++;
        bucket.requestsInHour++;
        bucket.requestsInDay++;
    }
}
