package opus.social.app.reporteai.adapters.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro para implementar rate limiting usando Bucket4j
 * Limita requisições por IP ou usuário
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Configuração: 100 requisições por minuto
    private final Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));

    // Endpoints que não devem ter rate limiting
    private final String[] excludedPatterns = {
        "/api/v1/auth/health",
        "/swagger-ui",
        "/v3/api-docs"
    };

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Verificar se endpoint está excluído
        if (isExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = getClientKey(request);
        Bucket bucket = buckets.computeIfAbsent(key, k -> Bucket4j.builder()
            .addLimit(limit)
            .build());

        if (bucket.tryConsume(1)) {
            // Adicionar headers informativos
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Muitas requisições. Tente novamente depois.\"}"
            );
        }
    }

    /**
     * Obtém a chave do cliente (IP ou usuário autenticado)
     */
    private String getClientKey(HttpServletRequest request) {
        // Preferir usuário autenticado
        if (request.getUserPrincipal() != null) {
            return "user:" + request.getUserPrincipal().getName();
        }

        // Usar IP do cliente
        String clientIP = request.getHeader("X-Forwarded-For");
        if (clientIP == null || clientIP.isEmpty()) {
            clientIP = request.getRemoteAddr();
        }
        return "ip:" + clientIP;
    }

    /**
     * Verifica se endpoint está excluído de rate limiting
     */
    private boolean isExcluded(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        for (String pattern : excludedPatterns) {
            if (requestPath.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
}
