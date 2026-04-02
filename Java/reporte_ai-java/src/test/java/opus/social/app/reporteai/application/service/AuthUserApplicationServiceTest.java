package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.application.dto.RegisterRequest;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthRoleJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthUserJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.AuthRoleRepository;
import opus.social.app.reporteai.infrastructure.persistence.repository.AuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AuthUserApplicationService
 */
@ExtendWith(MockitoExtension.class)
class AuthUserApplicationServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthRoleRepository authRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthUserApplicationService authUserService;

    private RegisterRequest registerRequest;
    private AuthRoleJpaEntity userRole;
    private AuthUserJpaEntity authUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
            "testuser",
            "test@example.com",
            "password123",
            "password123",
            "Test User"
        );

        userRole = AuthRoleJpaEntity.builder()
            .id(UUID.randomUUID())
            .roleName("USER")
            .isActive(true)
            .build();

        authUser = AuthUserJpaEntity.builder()
            .id(UUID.randomUUID())
            .username("testuser")
            .email("test@example.com")
            .passwordHash("hashedPassword")
            .fullName("Test User")
            .isActive(true)
            .isLocked(false)
            .failedLoginAttempts(0)
            .roles(new HashSet<>())
            .build();
    }

    @Test
    void testRegisterUserSuccess() {
        // Arrange
        when(authUserRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(authUserRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword");
        when(authRoleRepository.findByRoleName("USER")).thenReturn(Optional.of(userRole));
        when(authUserRepository.save(any())).thenReturn(authUser);

        // Act
        AuthUserJpaEntity result = authUserService.registerUser(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(authUserRepository, times(1)).save(any());
    }

    @Test
    void testRegisterUserWithMismatchedPasswords() {
        // Arrange
        registerRequest.setPasswordConfirm("wrongPassword");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authUserService.registerUser(registerRequest));
    }

    @Test
    void testRegisterUserWithExistingUsername() {
        // Arrange
        when(authUserRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authUserService.registerUser(registerRequest));
    }

    @Test
    void testRegisterUserWithExistingEmail() {
        // Arrange
        when(authUserRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(authUserRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authUserService.registerUser(registerRequest));
    }

    @Test
    void testGetUserByUsername() {
        // Arrange
        when(authUserRepository.findByUsernameWithRoles("testuser"))
            .thenReturn(Optional.of(authUser));

        // Act
        AuthUserJpaEntity result = authUserService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetUserByUsernameNotFound() {
        // Arrange
        when(authUserRepository.findByUsernameWithRoles(anyString()))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authUserService.getUserByUsername("nonexistent"));
    }

    @Test
    void testRecordFailedLoginAttempt() {
        // Arrange
        authUser.setFailedLoginAttempts(0);
        when(authUserRepository.findByUsernameWithRoles("testuser"))
            .thenReturn(Optional.of(authUser));
        when(authUserRepository.save(any())).thenReturn(authUser);

        // Act
        authUserService.recordFailedLoginAttempt("testuser");

        // Assert
        verify(authUserRepository, times(1)).save(any());
    }

    @Test
    void testRecordFailedLoginAttemptLockAfter5Attempts() {
        // Arrange
        authUser.setFailedLoginAttempts(4);
        when(authUserRepository.findByUsernameWithRoles("testuser"))
            .thenReturn(Optional.of(authUser));
        when(authUserRepository.save(any())).thenReturn(authUser);

        // Act
        authUserService.recordFailedLoginAttempt("testuser");

        // Assert
        assertTrue(authUser.getIsLocked());
        assertNotNull(authUser.getLockedUntil());
        verify(authUserRepository, times(1)).save(any());
    }

    @Test
    void testRecordSuccessfulLogin() {
        // Arrange
        authUser.setFailedLoginAttempts(2);
        authUser.setLastLoginAt(null);
        when(authUserRepository.findByUsernameWithRoles("testuser"))
            .thenReturn(Optional.of(authUser));
        when(authUserRepository.save(any())).thenReturn(authUser);

        // Act
        authUserService.recordSuccessfulLogin("testuser");

        // Assert
        assertEquals(0, authUser.getFailedLoginAttempts());
        assertFalse(authUser.getIsLocked());
        assertNotNull(authUser.getLastLoginAt());
        verify(authUserRepository, times(1)).save(any());
    }

    @Test
    void testUnlockUser() {
        // Arrange
        authUser.setIsLocked(true);
        authUser.setFailedLoginAttempts(5);
        when(authUserRepository.findByUsernameWithRoles("testuser"))
            .thenReturn(Optional.of(authUser));
        when(authUserRepository.save(any())).thenReturn(authUser);

        // Act
        authUserService.unlockUser("testuser");

        // Assert
        assertFalse(authUser.getIsLocked());
        assertEquals(0, authUser.getFailedLoginAttempts());
        verify(authUserRepository, times(1)).save(any());
    }

    @Test
    void testUpdatePassword() {
        // Arrange
        String newPassword = "newPassword123";
        when(authUserRepository.findByUsernameWithRoles("testuser"))
            .thenReturn(Optional.of(authUser));
        when(passwordEncoder.encode(newPassword)).thenReturn("newHashedPassword");
        when(authUserRepository.save(any())).thenReturn(authUser);

        // Act
        authUserService.updatePassword("testuser", newPassword);

        // Assert
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(authUserRepository, times(1)).save(any());
    }

    @Test
    void testAddRoleToUser() {
        // Arrange
        AuthRoleJpaEntity adminRole = AuthRoleJpaEntity.builder()
            .id(UUID.randomUUID())
            .roleName("ADMIN")
            .isActive(true)
            .build();

        when(authUserRepository.findByUsernameWithRoles("testuser"))
            .thenReturn(Optional.of(authUser));
        when(authRoleRepository.findByRoleName("ADMIN"))
            .thenReturn(Optional.of(adminRole));
        when(authUserRepository.save(any())).thenReturn(authUser);

        // Act
        authUserService.addRoleToUser("testuser", "ADMIN");

        // Assert
        assertTrue(authUser.getRoles().contains(adminRole));
        verify(authUserRepository, times(1)).save(any());
    }

    @Test
    void testDeactivateUser() {
        // Arrange
        authUser.setIsActive(true);
        when(authUserRepository.findByUsernameWithRoles("testuser"))
            .thenReturn(Optional.of(authUser));
        when(authUserRepository.save(any())).thenReturn(authUser);

        // Act
        authUserService.deactivateUser("testuser");

        // Assert
        assertFalse(authUser.getIsActive());
        verify(authUserRepository, times(1)).save(any());
    }

    @Test
    void testActivateUser() {
        // Arrange
        authUser.setIsActive(false);
        when(authUserRepository.findByUsernameWithRoles("testuser"))
            .thenReturn(Optional.of(authUser));
        when(authUserRepository.save(any())).thenReturn(authUser);

        // Act
        authUserService.activateUser("testuser");

        // Assert
        assertTrue(authUser.getIsActive());
        verify(authUserRepository, times(1)).save(any());
    }
}
