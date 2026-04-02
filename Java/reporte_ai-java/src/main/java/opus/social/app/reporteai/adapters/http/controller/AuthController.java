package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.adapters.security.JwtTokenProvider;
import opus.social.app.reporteai.application.dto.RegisterRequest;
import opus.social.app.reporteai.application.service.AuthUserApplicationService;
import opus.social.app.reporteai.application.service.TokenBlacklistService;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthUserJpaEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para Autenticação
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e autorização")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuthUserApplicationService authUserService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            AuthUserApplicationService authUserService,
            TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.authUserService = authUserService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    /**
     * Registrar novo usuário
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou username/email já existem"),
        @ApiResponse(responseCode = "409", description = "Username ou email já registrado")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Validar senhas
            if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirm())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "PASSWORDS_DONT_MATCH");
                errorResponse.put("message", "Senhas não correspondem");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Registrar usuário
            AuthUserJpaEntity newUser = authUserService.registerUser(registerRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Usuário registrado com sucesso");
            response.put("userId", newUser.getId());
            response.put("username", newUser.getUsername());
            response.put("email", newUser.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "REGISTRATION_FAILED");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    /**
     * Login - retorna token JWT
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica um usuário e retorna um token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
        @ApiResponse(responseCode = "400", description = "Credenciais inválidas"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // Registrar login bem-sucedido no banco
            authUserService.recordSuccessfulLogin(loginRequest.getUsername());

            String jwt = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(loginRequest.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", jwt);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 86400); // 24 horas em segundos
            response.put("username", loginRequest.getUsername());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Registrar tentativa falhada
            try {
                authUserService.recordFailedLoginAttempt(loginRequest.getUsername());
            } catch (Exception ignored) {
                // Ignorar se usuário não existe
            }

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "AUTHENTICATION_FAILED");
            errorResponse.put("message", "Credenciais inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Refresh token - retorna um novo access token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Gera um novo token JWT usando um refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token renovado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Refresh token inválido")
    })
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        try {
            if (!tokenProvider.validateToken(refreshRequest.getRefreshToken())) {
                throw new RuntimeException("Refresh token inválido");
            }

            String username = tokenProvider.getUsernameFromToken(refreshRequest.getRefreshToken());
            String newAccessToken = tokenProvider.generateTokenFromUsername(username);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 86400);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "INVALID_REFRESH_TOKEN");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Validar token
     */
    @PostMapping("/validate")
    @Operation(summary = "Validar token", description = "Valida um token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token válido"),
        @ApiResponse(responseCode = "400", description = "Token inválido")
    })
    public ResponseEntity<?> validateToken(@Valid @RequestBody ValidateTokenRequest validateRequest) {
        try {
            boolean isValid = tokenProvider.validateToken(validateRequest.getToken());
            
            Map<String, Object> response = new HashMap<>();
            response.put("isValid", isValid);
            
            if (isValid) {
                String username = tokenProvider.getUsernameFromToken(validateRequest.getToken());
                response.put("username", username);
                response.put("expiresAt", tokenProvider.getExpirationDateFromToken(validateRequest.getToken()));
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("isValid", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Logout - revoga o token atual
     */
    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Logout", description = "Realiza logout e revoga o token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        try {
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);

                // Validar e extrair informações do token
                if (tokenProvider.validateToken(token)) {
                    String username = tokenProvider.getUsernameFromToken(token);
                    AuthUserJpaEntity user = authUserService.getUserByUsername(username);

                    // Adicionar token ao blacklist
                    tokenBlacklistService.blacklistTokenForLogout(
                        token,
                        user.getId(),
                        "ACCESS",
                        tokenProvider.getExpirationDateFromToken(token).toLocalDateTime()
                    );
                }
            }

            // Limpar contexto de segurança
            SecurityContextHolder.clearContext();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Logout realizado com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "LOGOUT_FAILED");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Health check - endpoint público para verificar se o servidor está rodando
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica se o servidor está operacional")
    @ApiResponse(responseCode = "200", description = "Servidor está rodando")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Servidor está operacional");
        return ResponseEntity.ok(response);
    }

    /**
     * DTOs para requisições
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {}

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RefreshTokenRequest {
        private String refreshToken;

        public RefreshTokenRequest() {}

        public RefreshTokenRequest(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class ValidateTokenRequest {
        private String token;

        public ValidateTokenRequest() {}

        public ValidateTokenRequest(String token) {
            this.token = token;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
