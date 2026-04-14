package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.application.dto.RegisterRequest;
import opus.social.app.reporteai.domain.exception.BusinessException;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthRoleJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthUserJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.AuthRoleRepository;
import opus.social.app.reporteai.infrastructure.persistence.repository.AuthUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Serviço de aplicação para usuários de autenticação
 */
@Service
@Transactional
public class AuthUserApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthUserApplicationService.class);

    // Padrão de senha forte: min 12 chars, números, letras maiúsculas/minúsculas, símbolos
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()\\[\\]{};:'\",.<>?/\\\\|`~-])(?=\\S+$).{12,}$"
    );

    private final AuthUserRepository authUserRepository;
    private final AuthRoleRepository authRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogApplicationService auditLogService;

    public AuthUserApplicationService(
            AuthUserRepository authUserRepository,
            AuthRoleRepository authRoleRepository,
            PasswordEncoder passwordEncoder,
            AuditLogApplicationService auditLogService) {
        this.authUserRepository = authUserRepository;
        this.authRoleRepository = authRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    /**
     * Registra um novo usuário com validações de segurança
     */
    public AuthUserJpaEntity registerUser(RegisterRequest request) {
        try {
            // Validar força da senha
            validatePasswordStrength(request.getPassword());

            // Verificar se username já existe
            if (authUserRepository.existsByUsername(request.getUsername())) {
                logger.warn("Registration attempt with existing username: {}", request.getUsername());
                throw new BusinessException("Username já está em uso");
            }

            // Verificar se email já existe
            if (authUserRepository.existsByEmail(request.getEmail())) {
                logger.warn("Registration attempt with existing email: {}", request.getEmail());
                throw new BusinessException("Email já está registrado");
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
                .roles(new HashSet<>())
                .build();

            // Atribuir role padrão (USER)
            AuthRoleJpaEntity userRole = authRoleRepository.findByRoleName("USER")
                .orElseThrow(() -> new BusinessException("Configuração interna: Role USER não encontrada"));
            user.getRoles().add(userRole);
 
            AuthUserJpaEntity savedUser = authUserRepository.save(user);
            logger.info("New user registered successfully: {}", request.getUsername());
            auditLogService.logUserRegistration(request.getUsername(), request.getEmail());

            return savedUser;

        } catch (BusinessException ex) {
            auditLogService.logSecurityIncident(
                "USER_REGISTRATION_FAILED",
                "Falha no registro de usuário: " + ex.getMessage()
            );
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error during user registration: {}", request.getUsername(), ex);
            auditLogService.logSecurityIncident(
                "USER_REGISTRATION_ERROR",
                "Erro inesperado durante registro: " + request.getUsername()
            );
            throw new BusinessException("Erro ao registrar usuário. Tente novamente mais tarde.");
        }
    }

    /**
     * Valida a força da senha
     * Requer: mínimo 12 caracteres, números, maiúsculas, minúsculas e símbolos
     */
    private void validatePasswordStrength(String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(
                "Senha fraca. Deve conter: mínimo 12 caracteres, " +
                "números, letras maiúsculas, minúsculas e símbolos especiais"
            );
        }
    }

    /**
     * Buscar usuário por username
     */
    public AuthUserJpaEntity getUserByUsername(String username) {
        return authUserRepository.findByUsernameWithRoles(username)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));
    }

    /**
     * Buscar usuário por email
     */
    public AuthUserJpaEntity getUserByEmail(String email) {
        return authUserRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
    }

    /**
     * Buscar todos os usuários ativos
     */
    public List<AuthUserJpaEntity> getAllActiveUsers() {
        return authUserRepository.findAllActiveUsers();
    }

    /**
     * Registrar tentativa de login falhada
     */
    public void recordFailedLoginAttempt(String username) {
        AuthUserJpaEntity user = getUserByUsername(username);
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

        // Bloquear após 5 tentativas falhas por 30 minutos
        if (user.getFailedLoginAttempts() >= 5) {
            user.setIsLocked(true);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
            auditLogService.logAccountLock(
                username,
                "Múltiplas tentativas de login falhadas"
            );
        } else {
            auditLogService.logFailedLoginAttempt(
                username,
                "Tentativa " + user.getFailedLoginAttempts() + " de 5"
            );
        }

        authUserRepository.save(user);
    }

    /**
     * Registrar login bem-sucedido
     */
    public void recordSuccessfulLogin(String username) {
        AuthUserJpaEntity user = getUserByUsername(username);
        user.setLastLoginAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        user.setIsLocked(false);
        user.setLockedUntil(null);

        authUserRepository.save(user);
        auditLogService.logSuccessfulLogin(username);
    }

    /**
     * Desbloquear usuário
     */
    public void unlockUser(String username) {
        AuthUserJpaEntity user = getUserByUsername(username);
        user.setIsLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        authUserRepository.save(user);
    }

    /**
     * Atualizar senha
     */
    public void updatePassword(String username, String newPassword) {
        AuthUserJpaEntity user = getUserByUsername(username);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        authUserRepository.save(user);
        auditLogService.logPasswordChange(username);
    }

    /**
     * Adicionar role ao usuário
     */
    public void addRoleToUser(String username, String roleName) {
        AuthUserJpaEntity user = getUserByUsername(username);
        AuthRoleJpaEntity role = authRoleRepository.findByRoleName(roleName)
            .orElseThrow(() -> new RuntimeException("Role não encontrada: " + roleName));

        user.getRoles().add(role);
        authUserRepository.save(user);
        auditLogService.logAuditEvent(
            AuditLogApplicationService.AuditEventType.ROLE_ASSIGNMENT,
            "USER",
            username,
            "Role '" + roleName + "' adicionada"
        );
    }

    /**
     * Remover role do usuário
     */
    public void removeRoleFromUser(String username, String roleName) {
        AuthUserJpaEntity user = getUserByUsername(username);
        AuthRoleJpaEntity role = authRoleRepository.findByRoleName(roleName)
            .orElseThrow(() -> new RuntimeException("Role não encontrada: " + roleName));

        user.getRoles().remove(role);
        authUserRepository.save(user);
        auditLogService.logAuditEvent(
            AuditLogApplicationService.AuditEventType.ROLE_ASSIGNMENT,
            "USER",
            username,
            "Role '" + roleName + "' removida"
        );
    }

    /**
     * Desativar usuário
     */
    public void deactivateUser(String username) {
        AuthUserJpaEntity user = getUserByUsername(username);
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());

        authUserRepository.save(user);
    }

    /**
     * Ativar usuário
     */
    public void activateUser(String username) {
        AuthUserJpaEntity user = getUserByUsername(username);
        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now());

        authUserRepository.save(user);
    }

    /**
     * Deletar usuário
     */
    public void deleteUser(UUID userId) {
        authUserRepository.deleteById(userId);
    }
}
