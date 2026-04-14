package opus.social.app.reporteai.adapters.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuração de Cache
 * Caching Strategy - Otimização de performance com múltiplos níveis
 *
 * Caches configurados:
 * - users: Cache de usuários por username
 * - activeUsers: Cache de lista de usuários ativos
 * - permissions: Cache de permissões por role
 * - occurrences: Cache de ocorrências por ID
 */
@Configuration
@EnableCaching
public class CacheConfig {
    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    /**
     * Gerenciador de cache em memória
     * Em produção, considerar migração para Redis
     */
    @Bean
    public CacheManager cacheManager() {
        logger.info("Configurando CacheManager com ConcurrentMapCacheManager");

        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
            "users",           // Cache de usuários por username
            "activeUsers",     // Cache de usuários ativos
            "permissions",     // Cache de permissões
            "occurrences",     // Cache de ocorrências
            "roles",           // Cache de roles
            "reports"          // Cache de relatórios
        );

        logger.info("CacheManager configurado com 6 caches: {}", cacheManager.getCacheNames());
        return cacheManager;
    }

    /**
     * Nota: Para migração para Redis em produção, substitua o bean acima por:
     *
     * @Bean
     * public LettuceConnectionFactory redisConnectionFactory() {
     *     return new LettuceConnectionFactory();
     * }
     *
     * @Bean
     * public RedisCacheManager cacheManager(LettuceConnectionFactory connectionFactory) {
     *     return RedisCacheManager.create(connectionFactory);
     * }
     *
     * E adicione ao pom.xml:
     * <dependency>
     *     <groupId>org.springframework.boot</groupId>
     *     <artifactId>spring-boot-starter-data-redis</artifactId>
     * </dependency>
     */
}
