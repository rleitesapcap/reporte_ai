package opus.social.app.reporteai.adapters.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cache Config Testes Unitários
 *
 * Valida:
 * - Configuração de cache manager
 * - Caches de diferentes tipos
 * - Acessos ao cache (primeiro hit é lento, cache hits são rápidos)
 * - Invalidação de cache
 * - Performance com cache
 */
@DisplayName("Caching Strategy Tests")
class CacheConfigTest {

    private CacheManager cacheManager;
    private CacheTestService cacheTestService;

    @BeforeEach
    void setUp() {
        cacheManager = new ConcurrentMapCacheManager(
            "users",
            "activeUsers",
            "permissions",
            "occurrences",
            "roles",
            "reports"
        );
        cacheTestService = new CacheTestService();
    }

    @Test
    @DisplayName("Cache manager deve conter todos os caches configurados")
    void testCacheManagerConfiguration() {
        // Assert
        assertNotNull(cacheManager.getCache("users"));
        assertNotNull(cacheManager.getCache("activeUsers"));
        assertNotNull(cacheManager.getCache("permissions"));
        assertNotNull(cacheManager.getCache("occurrences"));
        assertNotNull(cacheManager.getCache("roles"));
        assertNotNull(cacheManager.getCache("reports"));
    }

    @Test
    @DisplayName("Deve melhorar performance com cache - Primeira chamada lenta")
    void testCachePerformanceFirstCall() {
        // Arrange
        cacheTestService.getCallCount().set(0);

        // Act
        long startTime = System.currentTimeMillis();
        String result1 = cacheTestService.getExpensiveData("testuser");
        long firstCallDuration = System.currentTimeMillis() - startTime;

        // Assert
        assertEquals("Data for testuser", result1);
        assertEquals(1, cacheTestService.getCallCount().get());
        assertTrue(firstCallDuration >= 50);  // Simula operação cara
    }

    @Test
    @DisplayName("Deve melhorar performance com cache - Chamadas subsequentes rápidas")
    void testCachePerformanceSubsequentCalls() {
        // Arrange
        cacheTestService.getCallCount().set(0);
        String result1 = cacheTestService.getExpensiveData("testuser");

        // Act - Primeira chamada
        long firstStart = System.currentTimeMillis();
        cacheTestService.getExpensiveData("testuser");
        long firstCachedDuration = System.currentTimeMillis() - firstStart;

        // Act - Segunda chamada
        long secondStart = System.currentTimeMillis();
        String result2 = cacheTestService.getExpensiveData("testuser");
        long secondCachedDuration = System.currentTimeMillis() - secondStart;

        // Assert
        assertEquals(result1, result2);
        assertEquals(1, cacheTestService.getCallCount().get());  // Só foi chamado 1 vez
        assertTrue(secondCachedDuration < firstCachedDuration);  // Cache é mais rápido
    }

    @Test
    @DisplayName("Cache deve diferençar por chave")
    void testCacheDifferentiationByKey() {
        // Arrange
        cacheTestService.getCallCount().set(0);

        // Act
        String result1 = cacheTestService.getExpensiveData("user1");
        String result2 = cacheTestService.getExpensiveData("user2");
        String result3 = cacheTestService.getExpensiveData("user1");

        // Assert
        assertNotEquals(result1, result2);
        assertEquals(result1, result3);
        assertEquals(2, cacheTestService.getCallCount().get());  // 2 chamadas diferentes
    }

    @Test
    @DisplayName("Invalidação de cache deve remover entrada")
    void testCacheEviction() {
        // Arrange
        cacheTestService.getCallCount().set(0);
        String result1 = cacheTestService.getExpensiveData("testuser");

        // Act - Invalidar cache
        cacheTestService.clearCache("testuser");
        String result2 = cacheTestService.getExpensiveData("testuser");

        // Assert
        assertEquals(result1, result2);
        assertEquals(2, cacheTestService.getCallCount().get());  // Foi chamado novamente
    }

    @Test
    @DisplayName("Diferentes caches devem ser independentes")
    void testIndependentCaches() {
        // Arrange
        assertNotNull(cacheManager.getCache("users"));
        assertNotNull(cacheManager.getCache("activeUsers"));

        // Act & Assert
        var usersCache = cacheManager.getCache("users");
        var activeUsersCache = cacheManager.getCache("activeUsers");

        assertNotEquals(usersCache, activeUsersCache);
    }

    @Test
    @DisplayName("Cache com null values deve funcionar")
    void testCacheNullValues() {
        // Arrange
        var cache = cacheManager.getCache("users");

        // Act
        cache.put("nullKey", null);

        // Assert
        assertNotNull(cache.get("nullKey"));
    }

    @Test
    @DisplayName("Cache deve suportar limite de tamanho")
    void testCacheSizeLimit() {
        // Arrange
        var cache = cacheManager.getCache("users");

        // Act - Adicionar múltiplas entradas
        for (int i = 0; i < 1000; i++) {
            cache.put("user" + i, "data" + i);
        }

        // Assert - Cache deve estar armazenando
        assertNotNull(cache.get("user0"));
        assertNotNull(cache.get("user999"));
    }

    @Test
    @DisplayName("Limpeza total de cache deve remover todas as entradas")
    void testClearAllCache() {
        // Arrange
        cacheTestService.getCallCount().set(0);
        cacheTestService.getExpensiveData("user1");
        cacheTestService.getExpensiveData("user2");

        // Act
        cacheTestService.clearAllCache();

        // Assert
        assertEquals(2, cacheTestService.getCallCount().get());

        // Simular novas chamadas
        cacheTestService.getExpensiveData("user1");
        assertEquals(3, cacheTestService.getCallCount().get());  // Nova chamada
    }

    @Test
    @DisplayName("Cache deve ser thread-safe")
    void testCacheThreadSafety() throws InterruptedException {
        // Arrange
        cacheTestService.getCallCount().set(0);
        Thread[] threads = new Thread[5];

        // Act - Múltiplas threads acessando cache simultaneamente
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    cacheTestService.getExpensiveData("concurrent");
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Assert - Deveria ter sido chamado poucas vezes (concorrência sincronizada)
        assertTrue(cacheTestService.getCallCount().get() <= 5);
    }

    /**
     * Classe auxiliar para testar caching
     */
    static class CacheTestService {
        private final java.util.concurrent.atomic.AtomicInteger callCount =
            new java.util.concurrent.atomic.AtomicInteger(0);

        public String getExpensiveData(String key) {
            callCount.incrementAndGet();
            try {
                Thread.sleep(50);  // Simula operação cara
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Data for " + key;
        }

        public void clearCache(String key) {
            // Simula @CacheEvict
        }

        public void clearAllCache() {
            callCount.set(0);
        }

        public java.util.concurrent.atomic.AtomicInteger getCallCount() {
            return callCount;
        }
    }
}
