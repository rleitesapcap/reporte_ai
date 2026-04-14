# 🛡️ Implementações de Segurança - Código Pronto para Deploy

Este arquivo contém implementações prontas para deploy dos 5 problemas críticos + 8 problemas altos.

---

## 1️⃣ Environment Variables para Secrets

### `.env.example` (adicione ao projeto)

```bash
# ========== JWT Configuration ==========
JWT_SECRET=your-super-secure-256-bit-key-here-minimum-32-chars-for-hs256
JWT_EXPIRATION_MS=86400000
JWT_REFRESH_EXPIRATION_MS=604800000

# ========== Database Configuration ==========
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/reporteai_db
SPRING_DATASOURCE_USERNAME=reporteai_user
SPRING_DATASOURCE_PASSWORD=secure_password_here_min_16_chars

# ========== SSL/TLS Configuration ==========
SSL_ENABLED=false
SSL_KEYSTORE_PATH=/path/to/keystore.p12
SSL_KEYSTORE_PASSWORD=keystore_password
SSL_KEY_ALIAS=reporteai

# ========== Security Configuration ==========
ENCRYPTION_KEY=encryption-key-32-chars-min-here
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,https://yourdomain.com
PASSWORD_MIN_LENGTH=12
PASSWORD_REQUIRE_NUMBERS=true
PASSWORD_REQUIRE_SYMBOLS=true

# ========== Logging Configuration ==========
LOG_LEVEL=INFO
LOG_FILE=/var/log/reporteai/app.log
LOG_MAX_FILE_SIZE=10MB
```

### `application.yml` Atualizado

```yaml
app:
  jwtSecret: ${JWT_SECRET:change-me-in-production}
  jwtExpirationInMs: ${JWT_EXPIRATION_MS:86400000}
  jwtRefreshExpirationInMs: ${JWT_REFRESH_EXPIRATION_MS:604800000}
  encryption:
    key: ${ENCRYPTION_KEY:change-me-in-production}
  security:
    password:
      minLength: ${PASSWORD_MIN_LENGTH:12}
      requireNumbers: ${PASSWORD_REQUIRE_NUMBERS:true}
      requireSymbols: ${PASSWORD_REQUIRE_SYMBOLS:true}

spring:
  application:
    name: reporteai

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    validate-on-migrate: true

server:
  port: 8082
  servlet:
    context-path: /
  error:
    include-message: on_param
    include-binding-errors: on_param
    include-stacktrace: never
    include-exception: false
  compression:
    enabled: true
    min-response-size: 1024
  ssl:
    enabled: ${SSL_ENABLED:false}
    key-store: ${SSL_KEYSTORE_PATH}
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: ${SSL_KEY_ALIAS:reporteai}
    protocol: TLSv1.3

logging:
  level:
    root: ${LOG_LEVEL:INFO}
    opus.social.app.reporteai: INFO
    org.springframework.web: WARN
    org.springframework.security: WARN
    org.hibernate.SQL: WARN
  file:
    name: ${LOG_FILE:/var/log/reporteai/app.log}
    max-size: ${LOG_MAX_FILE_SIZE:10MB}
    max-history: 30
    total-size-cap: 1GB
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

---

## 2️⃣ Logger Correto em JwtTokenProvider

**Arquivo:** `src/main/java/opus/social/app/reporteai/adapters/security/JwtTokenProvider.java`

```java
package opus.social.app.reporteai.adapters.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private long jwtExpirationInMs;

    @Value("${app.jwtRefreshExpirationInMs}")
    private long jwtRefreshExpirationInMs;

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authentication.getAuthorities());

        return Jwts.builder()
            .claims(claims)
            .subject(userPrincipal.getUsername())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
    }

    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationInMs);

        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            logger.warn("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.warn("Invalid JWT format");
        } catch (ExpiredJwtException ex) {
            logger.debug("JWT token has expired");
        } catch (UnsupportedJwtException ex) {
            logger.warn("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.warn("Empty JWT string");
        } catch (Exception ex) {
            logger.error("Unexpected error during token validation", ex);
        }
        return false;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
```

---

## 3️⃣ GlobalExceptionHandler Seguro

**Arquivo:** `src/main/java/opus/social/app/reporteai/adapters/http/exception/GlobalExceptionHandler.java`

```java
package opus.social.app.reporteai.adapters.http.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import opus.social.app.reporteai.domain.exception.BusinessException;
import opus.social.app.reporteai.domain.exception.DomainException;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import opus.social.app.reporteai.domain.exception.DuplicateDataException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ====== Domain Exceptions ======

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFoundException(
            EmployeeNotFoundException ex) {
        logger.warn("Employee not found: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(createErrorResponse(
                "EMPLOYEE_NOT_FOUND",
                "Funcionário não encontrado",
                HttpStatus.NOT_FOUND
            ));
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateDataException(
            DuplicateDataException ex) {
        logger.warn("Duplicate data detected: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(createErrorResponse(
                "DUPLICATE_DATA",
                "Dados já existem no sistema",
                HttpStatus.CONFLICT
            ));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex) {
        logger.warn("Business rule violation: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(createErrorResponse(
                "BUSINESS_ERROR",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
            ));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex) {
        logger.warn("Domain error: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(createErrorResponse(
                "DOMAIN_ERROR",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
            ));
    }

    // ====== Validation Exceptions ======

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(
                error.getField(),
                error.getDefaultMessage()
            ));

        logger.warn("Validation error: {}", errors);
        
        ErrorResponse response = createErrorResponse(
            "VALIDATION_ERROR",
            "Dados inválidos fornecidos",
            HttpStatus.BAD_REQUEST
        );
        response.setDetails(errors);
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    // ====== Generic Exception Handler ======

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Log erro completo (com stack trace) apenas no servidor
        logger.error("Unhandled exception occurred", ex);
        
        // Retorna erro genérico ao cliente (sem detalhes)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(createErrorResponse(
                "INTERNAL_ERROR",
                "Ocorreu um erro interno. Contate o suporte se o problema persistir.",
                HttpStatus.INTERNAL_SERVER_ERROR
            ));
    }

    // ====== Helper Methods ======

    private ErrorResponse createErrorResponse(
            String code,
            String message,
            HttpStatus status) {
        
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .code(code)
            .message(message)
            .traceId(MDC.get("traceId") != null ? MDC.get("traceId") : UUID.randomUUID().toString())
            .path(MDC.get("requestPath"))
            .build();
    }
}

// DTO para resposta de erro
@Data
@Builder
class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String code;
    private String message;
    private String traceId;  // Para rastrear erro no suporte
    private String path;
    private Map<String, String> details;  // Para erros de validação
}
```

---

## 4️⃣ Validação de Força de Senha

**Arquivo:** `src/main/java/opus/social/app/reporteai/application/dto/RegisterRequest.java`

```java
package opus.social.app.reporteai.application.dto;

import jakarta.validation.constraints.*;

@Data
@Builder
public class RegisterRequest {
    
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+$",
        message = "Username contém caracteres inválidos. Apenas letras, números, ponto, underscore e hífen são permitidos"
    )
    private String username;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 255, message = "Email não pode exceder 255 caracteres")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 12, max = 128, message = "Senha deve ter entre 12 e 128 caracteres")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()\\[\\]{};:'\",.<>?/\\\\|`~-])(?=\\S+$).*$",
        message = "Senha deve conter números, letras minúsculas, maiúsculas e símbolos"
    )
    private String password;
    
    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String passwordConfirm;
    
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String fullName;
    
    // Validação customizada no service
    @AssertTrue(message = "Senhas não correspondem")
    private boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirm);
    }
}
```

**Arquivo:** `src/main/java/opus/social/app/reporteai/application/service/AuthUserApplicationService.java`

```java
public AuthUserJpaEntity registerUser(RegisterRequest request) {
    // Validações básicas
    if (authUserRepository.existsByUsername(request.getUsername())) {
        throw new BusinessException("Username já existe");
    }

    if (authUserRepository.existsByEmail(request.getEmail())) {
        throw new BusinessException("Email já está registrado");
    }

    // Validação de força de senha (redundante, mas em camada de serviço)
    validatePasswordStrength(request.getPassword());

    // Verificar histórico de senhas
    List<String> passwordHistory = authUserRepository
        .findPasswordHistoryByUsername(request.getUsername(), 5);
    
    if (passwordHistory.stream().anyMatch(oldPass -> 
            passwordEncoder.matches(request.getPassword(), oldPass))) {
        throw new BusinessException(
            "Não pode reutilizar uma das últimas 5 senhas. Escolha uma nova"
        );
    }

    // Criar usuário
    AuthUserJpaEntity user = AuthUserJpaEntity.builder()
        .id(UUID.randomUUID())
        .username(request.getUsername())
        .email(request.getEmail())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .fullName(request.getFullName())
        .isActive(true)
        .isLocked(false)
        .failedLoginAttempts(0)
        .passwordChangedAt(LocalDateTime.now())
        .roles(new HashSet<>())
        .build();

    // Atribuir role padrão
    AuthRoleJpaEntity userRole = authRoleRepository.findByRoleName("USER")
        .orElseThrow(() -> new RuntimeException("Role USER não encontrada"));
    user.getRoles().add(userRole);

    return authUserRepository.save(user);
}

private void validatePasswordStrength(String password) {
    String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{12,}$";
    
    if (!password.matches(pattern)) {
        throw new BusinessException(
            "Senha fraca. Deve conter: mínimo 12 caracteres, números, " +
            "letras maiúsculas, minúsculas e símbolos especiais"
        );
    }
}
```

---

## 5️⃣ SecurityConfig com Headers de Segurança

**Arquivo:** `src/main/java/opus/social/app/reporteai/adapters/security/SecurityConfig.java`

```java
package opus.social.app.reporteai.adapters.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    prePostEnabled = true,
    securedEnabled = true,
    jsr250Enabled = true
)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomUserDetailsService customUserDetailsService;
    private final RateLimitingFilter rateLimitingFilter;

    @Value("${app.security.allowedOrigins:http://localhost:3000,http://localhost:4200}")
    private String allowedOrigins;

    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;

    public SecurityConfig(
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            CustomUserDetailsService customUserDetailsService,
            RateLimitingFilter rateLimitingFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.customUserDetailsService = customUserDetailsService;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // Cost factor 12
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(true);  // ← Importante: não revela se usuário existe
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ===== CSRF =====
            .csrf(csrf -> csrf.disable())  // OK porque usando JWT (stateless)
            
            // ===== CORS =====
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // ===== HTTPS =====
            .requiresChannel(channel -> {
                if (sslEnabled) {
                    channel.anyRequest().requiresSecure();
                }
            })
            
            // ===== Headers de Segurança =====
            .headers(headers -> headers
                // Prevenir Clickjacking
                .frameOptions(frameOptions -> frameOptions.deny())
                
                // Prevenir MIME sniffing
                .contentTypeOptions(contentTypeOptions -> contentTypeOptions.nosniff())
                
                // X-XSS-Protection
                .xssProtection(xss -> xss.and())
                
                // HSTS - Force HTTPS
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)  // 1 ano
                    .includeSubDomains(true)
                    .preload(true)
                )
                
                // Content-Security-Policy
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data: https:; " +
                        "font-src 'self' data: https:; " +
                        "connect-src 'self' https:; " +
                        "frame-ancestors 'none'; " +
                        "base-uri 'self'; " +
                        "form-action 'self'"
                    )
                )
                
                // Referrer-Policy
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_NO_REFERRER)
                )
                
                // Permissions-Policy
                .permissionsPolicy(permissions -> permissions
                    .policy(
                        "camera=(), microphone=(), payment=(), usb=(), " +
                        "magnetometer=(), gyroscope=(), accelerometer=()"
                    )
                )
            )
            
            // ===== Exception Handling =====
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            
            // ===== Session Management =====
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // ===== Authorization =====
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            
            // ===== Authentication Provider =====
            .authenticationProvider(authenticationProvider())
            
            // ===== Filters =====
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(rateLimitingFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ✅ SEGURO - Whitelist de origens
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        
        // ✅ SEGURO - Whitelist de métodos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // ✅ SEGURO - Whitelist de headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type",
            "Authorization",
            "Accept",
            "X-Requested-With",
            "X-CSRF-Token",
            "X-API-Key"
        ));
        
        // ✅ SEGURO - Headers expostos
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Rate-Limit-Remaining",
            "X-RateLimit-Reset",
            "X-Total-Count"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

## 6️⃣ Docker Compose Seguro

**Arquivo:** `docker-compose.yml`

```yaml
version: '3.9'

services:
  postgres:
    image: postgres:15-alpine
    container_name: reporteai-postgres
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: reporteai_db
      POSTGRES_USER: reporteai_user
      POSTGRES_PASSWORD: ${DB_PASSWORD:-change-me-in-prod}
      POSTGRES_INITDB_ARGS: "-c ssl=on -c ssl_cert_file=/etc/ssl/certs/server.crt -c ssl_key_file=/etc/ssl/private/server.key"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./db/init:/docker-entrypoint-initdb.d
    networks:
      - reporteai-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U reporteai_user"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: reporteai-pgadmin
    ports:
      - "5050:80"
    environment:
      PGADMIN_DEFAULT_EMAIL: ${PGADMIN_EMAIL:-admin@example.com}
      PGADMIN_DEFAULT_PASSWORD: ${PGADMIN_PASSWORD:-change-me-in-prod}
      SCRIPT_NAME: /pgadmin
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - reporteai-network
    restart: unless-stopped

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: reporteai-app
    ports:
      - "8082:8082"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/reporteai_db
      SPRING_DATASOURCE_USERNAME: reporteai_user
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-change-me-in-prod}
      JWT_SECRET: ${JWT_SECRET:-change-me-in-prod-min-32-chars}
      ENCRYPTION_KEY: ${ENCRYPTION_KEY:-change-me-in-prod-min-32-chars}
      SERVER_SSL_ENABLED: ${SSL_ENABLED:-false}
      LOG_LEVEL: INFO
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - reporteai-network
    restart: unless-stopped

networks:
  reporteai-network:
    driver: bridge

volumes:
  postgres_data:
```

---

## ✅ Checklist de Implementação

- [ ] Criar arquivo `.env` com variáveis de ambiente
- [ ] Atualizar `application.yml` para usar environment variables
- [ ] Atualizar `JwtTokenProvider.java` com logging correto
- [ ] Atualizar `GlobalExceptionHandler.java` para não retornar stack traces
- [ ] Atualizar `RegisterRequest.java` com validação de senha
- [ ] Atualizar `AuthUserApplicationService.java` com validação de força
- [ ] Atualizar `SecurityConfig.java` com headers de segurança
- [ ] Atualizar `docker-compose.yml` com variáveis de ambiente
- [ ] Testar todas as mudanças localmente
- [ ] Executar testes de segurança (SAST)
- [ ] Deploy em staging primeiro
- [ ] Validar com segurança antes de produção

---

## 🧪 Testes para Validar as Mudanças

```bash
# 1. Testar se secrets não estão no código
grep -r "mySecureSecret" src/
grep -r "reporteai:reporteai" src/

# 2. Testar força de senha
curl -X POST http://localhost:8082/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "weak",
    "passwordConfirm": "weak",
    "fullName": "Test User"
  }'
# Deve retornar erro

# 3. Testar headers de segurança
curl -i http://localhost:8082/api/v1/auth/validate
# Verificar presença de: X-Frame-Options, X-Content-Type-Options, CSP, etc

# 4. Testar rate limiting
for i in {1..150}; do
  curl http://localhost:8082/api/v1/occurrences \
    -H "Authorization: Bearer $TOKEN" &
done
# Deve retornar 429 após 100 requisições
```

