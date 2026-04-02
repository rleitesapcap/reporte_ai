package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.application.dto.RegisterRequest;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthRoleJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthUserJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.AuthRoleRepository;
import opus.social.app.reporteai.infrastructure.persistence.repository.AuthUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Serviço de aplicação para usuários de autenticação
 */
@Service
@Transactional
public class AuthUserApplicationService {

    private final AuthUserRepository authUserRepository;
    private final AuthRoleRepository authRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthUserApplicationService(
            AuthUserRepository authUserRepository,
            AuthRoleRepository authRoleRepository,
            PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.authRoleRepository = authRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra um novo usuário
     */
    public AuthUserJpaEntity registerUser(RegisterRequest request) {
        // Validações
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new RuntimeException("Senhas não correspondem");
        }

        if (authUserRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username já existe");
        }

        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já está registrado");
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
            .orElseThrow(() -> new RuntimeException("Role USER não encontrada"));
        user.getRoles().add(userRole);

        return authUserRepository.save(user);
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
